loadModule("/TraceCompass/Trace");
loadModule('/TraceCompass/Analysis');
loadModule('/TraceCompass/DataProvider');
loadModule('/TraceCompass/View');

/* Global variables */
var lastTimestampValue = -1;
var filepath = "/home/adel/graph2.html";

/* Get the last attribute value */
function getLastValue(quark) {
    endTime = (lastTimestampValue > 0) ? lastTimestampValue : ss.getCurrentEndTime();
    startTime = ss.getStartTime();

    while (startTime < endTime) {
        currentInterval = ss.querySingleState(endTime, quark);
        stateValue = currentInterval.getValue();

        if (stateValue != null) {
            return stateValue;
        }
        endTime = currentInterval.getStartTime() - 1;
    }

    return stateValue;
}

/* Write to a file */
function dumpToFile(fileName, content) {
	var File = java.io.File;
	var FileWriter = java.io.FileWriter
	var BufferedWriter = java.io.BufferedWriter
	
    var file = new File(fileName);
    if (!file.exists()) {
	     file.createNewFile();
	}
    
    print("Writing to: " + fileName);
    var fw = new FileWriter(file);
    var bw = new BufferedWriter(fw);
    bw.write(content);
    bw.close();
    
}

/* Convert json format to marmaid markdown language */
function convertJsonToMermaidMarkdown(graph, html) {
    var htmlStartString =
    '<!DOCTYPE html>    \
    <html lang="en">    \
        <head> <meta charset="utf-8">    \
            <style type="text/css">    \
                .scrollHorizontal {     \
                    width:100%;         \
                    height:auto;        \
                    overflow-x:auto;    \
                }                       \
                .mermaidContainer {    \
                    display:flex;      \
                    justify-content: center;\
                }   \
                .centerDiv {    \
                    position: absolute;     \
                    top: 45%;               \
                    transform: translateY(-50%);    \
                    margin: 0 auto;                 \
                    min-width:2000px; \
                }    \
                br {    \
                    content: " ";    \
                    display: block;    \
                    margin: 0.3em;    \
                }    \
            </style>    \
        </head>    \
        <body>    \
            <h1>Super-pipeline Architecture</h1> <br><br><br>    \
            <div class="centerDiv">    \
            <div class="mermaid mermaidContainer"> ';

    var htmlEndString = 
    'classDef pipelineclass font-weight:bold;   \
    classDef tableclass fill:#F5D77B;   \
    </div>    \
    </div>    \
    <script src="https://cdn.jsdelivr.net/npm/mermaid/dist/mermaid.min.js">    \
    </script>    \
    <script>mermaid.initialize({startOnLoad:true});</script>    \
    </body>    \
    </html>';

    /**
     * Does not work with flowchart (bug)
     *      classDef exClass font-family:monospace
     *      classDef exClass font-style:bold
     *      class PIPELINE0 exClass
     */
    var mermaidScriptString = "\nflowchart TD\n";

    function typeOf(type, element) {
        return (element.type == type);
    }

    function groupOf(group, element) {
        return (element.group == group);
    }

    function splitDeviceName(devName, char) {
        values = devName.split(char);
        return {
                'first':values[0],
                'second':values.length >=2 ? values[1] : ''
            }
    }

    var subgraphString = '';
    pipelinesArray = graph.nodes.filter(typeOf.bind(this,"pipeline"));
    pipelinesArray.forEach(function(pipeline){

        pipelineName = pipeline.name;
        pipelineIndex = pipeline.id;

        subgraphString += '\nsubgraph ' + pipeline.name +'[' + pipeline.name +']\n';
        outsideString = "";

        tables = graph.nodes.filter(typeOf.bind(this,"table"))
                            .filter(groupOf.bind(this, pipelineIndex));

        tables.forEach(function (item) {
            subgraphString += item.name + '[[' + item.name + ']]\n';
            subgraphString += 'class ' + item.name + ' tableclass\n';
        });

        graph.links.forEach(function (link) {
            target = graph.nodes[link.target];
            source = graph.nodes[link.source];

            if(source.type == "input-port" && (target.type == "table" && target.group == pipelineIndex)) {
                if(source.name.startsWith("source")) {
                    outsideString += source.name + "((fa:fa-circle)) \n";
                    outsideString += "click " + source.name + " nope_function \"PCAP source file : <br>" +
                        source.name.split(':')[1] + "\"\n";
                }
                outsideString += source.name + '--' + link.value + '-->' + target.name + '\n';

                //FIXME : To use this code once mermaid solve the issue of changing link and node styles for flowchart graphs.
                // The goal of this code is use a single device name instead of using queues names.

                //devName = splitDeviceName(source.name, '/');
                //outsideString += devName.port + "--" + devName.queue + ': ' + link.value + '-->' + target.name + '\n';
            } 
            else if ((source.type == "pipeline" && link.source == pipelineIndex) && target.type == "output-port") {
                /* special visualization for sink type ports */
                if(target.name.startsWith("sink")) {
                    outsideString += target.name + "((fa:fa-circle)) \n";
                    name = splitDeviceName(target.name, ':');
                    if(isNaN(name.second))
                        outsideString += "click " + target.name + " nope_function \"saved to PCAP file : <br>" + target.name.split(':')[1] + "\"\n";
                }

                outsideString += source.name + '--' + link.value + '-->' + target.name + "\n";
                
                //FIXME : To use this code once mermaid solve the issue of changing link and node styles for flowchart graphs.
                //devName = splitDeviceName(target.name, '/');
                //outsideString += source.name + "--" +  devName.queue +': ' + link.value + '-->' + target.name + "\n";
            } 
            else if(source.type == "table" && target.type == "table" && 
                        source.group == pipelineIndex && target.group == pipelineIndex) {
                subgraphString += source.name + '--' + link.value + '-->' + target.name + '\n';
            }
        });

        subgraphString += 'end\n' +  '\n'  + 'class '  + pipeline.name + ' pipelineclass; \n' + outsideString;
    });

    mermaidScriptString += subgraphString + '\n';

    if(html == false)
        return '# Architecture of pipelines \n ```mermaid\n' + 
        mermaidScriptString.replace(/<br>/g, "") +
         '```';
    
    return htmlStartString + mermaidScriptString + htmlEndString;
}

// Check passed paramters
if(argv.length >= 1) {
    if(argv.length > 1) {
        throw ("Error parsing script paramters");
    }

    try {
        var lastTimestampValue = Number(argv[0]);
    } catch (e) {
        throw ("Error parsing script paramters");
    }
}

// Get the currently active trace
var trace = getActiveTrace();

if (trace == null) {
	  print("Trace is null");
	  exit();
}
ROOT_ATTRIBUTE = -1;
INVALID_ATTRIBUTE = -2;

// Get the Statistics module (by name) for that trace
var analysis = getTraceAnalysis(trace, 'DPDK Pipelines Analysis');
if (analysis == null) {
	  print("Problem fetching DPDK Pipeline Analysis");
	  exit();
}

var graph = {};
graph.nodes = [];
graph.links = [];
graph.groups = [];

var ss = analysis.getStateSystem();

if(ss == null) {
    print("Problem getting the statesystem");
    exit();
}

//browse all pipelines
var nodeIndex = 0;

pipelineQuarks = ss.getQuarkRelative(ROOT_ATTRIBUTE, "Pipelines")
quarks = ss.getQuarks(pipelineQuarks, "*");
for (i = 0; i < quarks.size(); i++) {
    pipelineQuark = quarks.get(i);

    pipelineName = ss.getAttributeName(pipelineQuark);
    graph.nodes[nodeIndex] = {"id": nodeIndex, "name": pipelineName, "type": "pipeline"};

    groupIndex = nodeIndex;
    nodeIndex += 1;

    portsQuarks = ss.getQuarkRelative(pipelineQuark, "Ports");
    //browse input ports
    inPortsMap = {};
    inportsQuarks = ss.getQuarkRelative(portsQuarks, "in-ports");
    inPortsListQuarks = ss.getQuarks(inportsQuarks, "*");
    for(j=0; j<inPortsListQuarks.size(); j++) {
	inPortQuark = inPortsListQuarks.get(j);
        inPortId = ss.getAttributeName(inPortQuark);

        nameQuark = ss.getQuarkRelative(inPortQuark, "name");
        nbRxQuark = ss.getQuarkRelative(inPortQuark, "nb_rx");
        nbDropQuark = ss.getQuarkRelative(inPortQuark, "nb_drop");

        inPortNameStr = getLastValue(nameQuark);
        nbRx = getLastValue(nbRxQuark);
        nbDrop = getLastValue(nbDropQuark);

        if(inPortNameStr != null) {
            graph.nodes[nodeIndex] = {"id": nodeIndex, "name": inPortNameStr, "type": "input-port"};

            //map the port id to its name
            inPortsMap[inPortId] = {"index": nodeIndex, "rx": nbRx, "rxAfterDrop": nbRx - nbDrop};
            nodeIndex += 1;
        }
    };


    /* browse output ports */
    outportsQuarks = ss.getQuarkRelative(portsQuarks, "out-ports");
    outPortsListQuarks = ss.getQuarks(outportsQuarks, "*");

    
    for(j=0; j<outPortsListQuarks.size(); j++) {
	outPortQuark = outPortsListQuarks.get(j);
	
        outPortId = ss.getAttributeName(outPortQuark);
        nameQuark = ss.getQuarkRelative(outPortQuark, "name");
        nbTxQuark = ss.getQuarkRelative(outPortQuark, "nb_tx");
        nbDropQuark = ss.getQuarkRelative(outPortQuark, "nb_drop");

        outPortNameStr = getLastValue(nameQuark);
        nbTx = getLastValue(nbTxQuark);
        nbDrop = getLastValue(nbDropQuark);

        if((outPortNameStr != null) && (nbTx != null) && (nbDrop != null)) {
            graph.nodes[nodeIndex] = {"id": nodeIndex, "name": outPortNameStr, "type": "output-port"};
            graph.links.push({"source":groupIndex, "target": nodeIndex, "value":
            (nbTx - nbDrop).toString()});

            nodeIndex += 1;
        } else {
            throw("Problem fetching values from the state system");
        }
    };

    /* browse tables */
    tableMap = {};
    tablesQuark = ss.optQuarkRelative(pipelineQuark, "Tables");

    if(tablesQuark == INVALID_ATTRIBUTE)
        continue;

    tableQuarks = ss.getQuarks(tablesQuark, "*").reverse();

    for(j=0; j<tableQuarks.size(); j++) {
	tabQuark = tableQuarks.get(j);
        tabId = ss.getAttributeName(tabQuark);
        nameQuark = ss.getQuarkRelative(tabQuark, "name");
        tabNameStr = getLastValue(nameQuark);

        if(tabNameStr != null) {
            graph.nodes[nodeIndex] = {"id": nodeIndex, "name": tabNameStr, "type": "table", "group": groupIndex};
            tableMap[tabId] = {"index": nodeIndex, "name": tabNameStr};

            // Explore connected input ports 
            inPortListQuarks = ss.optQuarkRelative(tabQuark, "in-ports");
            
            if(inPortListQuarks != INVALID_ATTRIBUTE) {
	            inputPortQuark = ss.getQuarks(inPortListQuarks, "*");
	
	            for(z = 0; z < inputPortQuark.size(); z++) {
	            	portQuark = inputPortQuark.get(z);
	                portId = ss.getAttributeName(portQuark);
	                portObject = inPortsMap[portId];
    
                    portIndex = portObject.index;
                    rxPkts = portObject.rx;
                    rxPktsAfterDrop = portObject.rxAfterDrop;

                    graph.links.push({"source":portIndex, "target": nodeIndex,
                        "value": rxPkts.toString()});
	            }
            }

            // Explore if there is a child table 
            childTableIdQuark = ss.optQuarkRelative(tabQuark, "child_table");
            childTableFwdQuark = ss.optQuarkRelative(tabQuark, "fwd_child_table");

            if((childTableIdQuark != INVALID_ATTRIBUTE) && (childTableFwdQuark != INVALID_ATTRIBUTE)) {
                childTableId = getLastValue(childTableIdQuark);
                childTableFwdValue = getLastValue(childTableFwdQuark);

                if((childTableId != null) && (childTableFwdValue != null)) {
                    childTableIndex = tableMap[childTableId].index;

                    graph.links.push({"source":nodeIndex, "target": childTableIndex, "value": childTableFwdValue})
                }
            }


            nodeIndex += 1;
        }
    };
}

//str = JSON.stringify(graph);
//dumpToFile("/home/adel/Desktop/vis/graph.json", str);

markdownString = convertJsonToMermaidMarkdown(graph, true /*html ?*/);
dumpToFile(filepath, markdownString);
