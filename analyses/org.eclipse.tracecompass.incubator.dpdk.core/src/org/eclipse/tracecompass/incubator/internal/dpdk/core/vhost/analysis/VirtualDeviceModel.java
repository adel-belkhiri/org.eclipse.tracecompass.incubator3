package org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.analysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;

/**
 * @author Adel Belkhiri
 *
 */
public class VirtualDeviceModel {

    private enum QueueType {
        UNKNOWN,
        RX,
        TX;
    }
    private class VirtQueueModel {
        //private final int fIndex;

        private QueueType fType;

        private int quark;
        private int size;
        //private int callFd;
        //private int kickFd;

        private int nbAvailableDescr;
        private long nbMbuff;

        public VirtQueueModel(/*int index, */QueueType type) {
            //this.fIndex = index;
            this.fType = type;

            this.nbAvailableDescr = 0;
            this.nbMbuff = 0;

        }

        public float computePercentageOfOccupancy(int availIndex, int lastAvailIndex) {

            nbAvailableDescr = availIndex - lastAvailIndex;

            if(availIndex < lastAvailIndex) {
                nbAvailableDescr += TWO_BYTES_LIMIT;
            }

            if(fType == QueueType.RX) {
                return (nbAvailableDescr * 100) / size;
            } else
                if(fType == QueueType.TX) {
                    return ((size - nbAvailableDescr) * 100) / size;
                }

            return 0;
        }
    }

    private final int fVid;
    private final int fConnfd;
    private final ITmfStateSystemBuilder fSs;
    private final int fQuark;
    private static final int TWO_BYTES_LIMIT = 65536;

    private final Map<Integer /*id*/, VirtQueueModel> fVirtQueues = new HashMap<>();
    private final Map<Integer /*pointer*/, VirtQueueModel> fEnabledVirtQueues = new HashMap<>();

    /**
     * Getter of connection fd
     * @return connfd
     */
    public int getConnfd() {
        return fConnfd;
    }

    /**
     * Getter of Vid
     * @return vid
     */
    public int getVid() {
        return fVid;
    }

    /**
     * @param vid
     * @param connfd
     * @param ss
     */
    public VirtualDeviceModel(int vid, int connfd, ITmfStateSystemBuilder ss, int deviceQuark) {
        this.fVid = vid;
        this.fConnfd = connfd;
        this.fSs = ss;

        /* attach the virtual device to the netdev in the state system */
        int vidsQuark;

        vidsQuark = fSs.getQuarkRelativeAndAdd(deviceQuark, IDpdkVhostModelAttributes.VIDS);
        this.fQuark = fSs.getQuarkRelativeAndAdd(vidsQuark, String.valueOf(this.fVid));

        int connfdQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkVhostModelAttributes.CONNFD);
        fSs.modifyAttribute(0, String.valueOf(this.fConnfd), connfdQuark);
    }

    /**
     * @param index xx
     * @param strType xx
     * @return
     */
    public boolean addQueue(int index, String strType) {
        QueueType type = QueueType.valueOf(strType.toUpperCase());
        if(!((type == QueueType.RX) || (type == QueueType.TX))) {
            return false;
        }

        if(!fVirtQueues.containsKey(index)) {

            VirtQueueModel queue = new VirtQueueModel(/*index,*/ type);
            fVirtQueues.put(index, queue);

            int virtQueuesQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, type.toString());
            queue.quark = fSs.getQuarkRelativeAndAdd(virtQueuesQuark, String.valueOf(index));

            if(type == QueueType.RX) {
                fSs.getQuarkRelativeAndAdd(queue.quark, IDpdkVhostModelAttributes.NB_MBUF_DEQUEUE);
            } else {
                fSs.getQuarkRelativeAndAdd(queue.quark, IDpdkVhostModelAttributes.NB_MBUF_ENQUEUE);
            }
            return true;
        }

        return false;
    }

    /**
     * @param pointer
     * @return
     * TODO: xx
     */
    public QueueType getQueueType(int pointer) {
        VirtQueueModel queue = fEnabledVirtQueues.get(pointer);
        if(queue != null) {
            return queue.fType;
        }
        return null;
    }

    /**
     * @param queuePointer
     * @param queueId
     * @param availIndex
     * @param lastAvailIndex
     * @param ts
     * @return
     */
    public void calculatePercentageOfQueueOccupancy(int queuePointer, int availIndex, int lastAvailIndex, long ts) {
        VirtQueueModel queue = fEnabledVirtQueues.get(queuePointer);
        if(queue != null) {
            int value = (int) queue.computePercentageOfOccupancy(availIndex, lastAvailIndex);
            int valueQuark = fSs.getQuarkRelativeAndAdd(queue.quark, IDpdkVhostModelAttributes.AVAIL_DESCR);
            assert(value >= 0);
            fSs.modifyAttribute(ts, value, valueQuark);
        }
    }

    /**
     * @param queuePointer
     * @param queueId
     * @param value
     * @param ts
     * @return
     */
    public void setNumberOfmBuffer(int queuePointer, int value, long ts) {
        VirtQueueModel queue = fEnabledVirtQueues.get(queuePointer);
        if(queue != null) {
            queue.nbMbuff += value;

            if(queue.fType == QueueType.RX) {
                int valueQuark = fSs.getQuarkRelativeAndAdd(queue.quark, IDpdkVhostModelAttributes.NB_MBUF_DEQUEUE);
                fSs.modifyAttribute(ts, queue.nbMbuff, valueQuark);
            } else {
                int valueQuark = fSs.getQuarkRelativeAndAdd(queue.quark, IDpdkVhostModelAttributes.NB_MBUF_ENQUEUE);
                fSs.modifyAttribute(ts, queue.nbMbuff, valueQuark);
            }
        }
    }

    /**
     * @param vringIdx
     * @param vqPointer
     * @param vqSize
     * @param vqCallFd
     * @param vqKickFd
     */
    public void setQueueEnabled(int vringIdx, int vqPointer, int vqSize /*, int vqCallFd, int vqKickFd */) {
            VirtQueueModel queue = fVirtQueues.get(vringIdx);
            if(queue != null) {
                queue.size = vqSize;
                fEnabledVirtQueues.put(vqPointer, queue);
            }
    }
}
