package org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;

/**
 * @author Adel Belkhiri
 *
 */

@SuppressWarnings("nls")
public class DpdkVhostAnalysisEventLayout {

    /**
     * This event Indicates the creation of a new virtual device
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventEthDevVhostCreate() {
        return new String("vhost_pmd:eth_dev_vhost_create");
    }

    /**
     * This event Indicates a new vhost_user connection
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventVhostUserAddConnection() {
        return new String("librte_vhost:vhost_user_add_connection");
    }

    /**
     * This event fires when a new virt queue is allocated
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventAllocVringQueue() {
        return new String("librte_vhost:alloc_vring_queue");
    }

    /**
     * This event fires when avirt queue is enabled
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventSetVringEnabled() {
        return new String("librte_vhost:set_vring_enabled");
    }

    /**
     * This event fires when the attributes of the virt queue were updated
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventQueueSetup() {
        return new String("vhost_pmd:queue_setup");
    }

    /**
     * This event fires when a set of mbuf was read from the RX ring
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventReadFromRxRing() {
        return new String("librte_vhost:read_from_rx_ring");
    }

    /**
     * This event fires when a set of mbuf was read from the RX ring
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventWriteToTxRing() {
        return new String("librte_vhost:write_to_tx_ring");
    }

    /**
     * This event fires when
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventDequeueBurst() {
        return new String("librte_vhost:dequeue_burst");
    }

    /**
     * This event fires when
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventEnqueueBurst() {
        return new String("librte_vhost:enqueue_burst");
    }

    /**
     * This event fires when an attached device was removed
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventDestroyDevice() {
        return new String("librte_vhost:destroy_device");
    }

    /**
    *
    * @return last available index
    * @since 3.0
    */
    public @NonNull String fieldLastAvailIdx() {
        return new String("last_avail_idx");
    }

    /**
    *
    * @return available index
    * @since 3.0
    */
    public @NonNull String fieldAvailIdx() {
        return new String("avail_idx");
    }

    /**
    *
    * @return the number of mbuf we want to read/send
    * @since 3.0
    */
    public @NonNull String fieldCount() {
        return new String("count");
    }

    /**
    *
    * @return the type of the queue : RX or TX
    * @since 3.0
    */
    public @NonNull String fieldTypeQueue() {
        return new String("type");
    }


    /**
    *
    * @return the index of the vring
    * @since 3.0
    */
    public @NonNull String fieldVringIdx() {
        return new String("vring_idx");
    }

    /**
    *
    * @return the id of the virtual queue
    * @since 3.0
    */
    public @NonNull String fieldVirtqueueId() {
        return new String("virtqueue_id");
    }

    /**
    *
    * @return the id of the virtual queue
    * @since 3.0
    */
    public @NonNull String fieldQueueId() {
        return new String("queue_id");
    }

    /**
    *
    * @return the number of mbuff that are actually read
    * @since 3.0
    */
    public @NonNull String fieldNbRx() {
        return new String("nb_rx");
    }

    /**
    *
    * @return the number of mbuff that are actually read
    * @since 3.0
    */
    public @NonNull String fieldNbTx() {
        return new String("nb_tx");
    }

    /**
    *
    * @return a pointer of the virtual queue
    * @since 3.0
    */
    public @NonNull String fieldVqPointer() {
        return new String("vq");
    }

    /**
    *
    * @return a pointer of the virtual queue
    * @since 3.0
    */
    public @NonNull String fieldSize() {
        return new String("size");
    }

    /**
    *
    * @return a pointer of the virtual queue
    * @since 3.0
    */
    public @NonNull String fieldCallFd() {
        return new String("callfd");
    }

    /**
    *
    * @return a pointer of the virtual queue
    * @since 3.0
    */
    public @NonNull String fieldKickFd() {
        return new String("kickfd");
    }

    /**
    *
    * @return the id of the attached virtual device
    * @since 3.0
    */
    public @NonNull String fieldVid() {
        return new String("vid");
    }

    /**
    *
    * @return the connection fd
    * @since 3.0
    */
    public @NonNull String fieldConnFd() {
        return new String("connfd");
    }

    /**
    *
    * @return the device name
    * @since 3.0
    */
    public @NonNull String fieldDevName() {
        return new String("dev_name");
    }
    /**
    *
    * @return the interface name
    * @since 3.0
    */
    public @NonNull String fieldIfceName() {
        return new String("ifce_name");
    }

    /**
    *
    * @return the hw address of the device
    * @since 3.0
    */
    public @NonNull String fieldHwAddr() {
        return new String("hw_addr");
    }

    /**
    *
    * @return the flags
    * @since 3.0
    */
    public @NonNull String fieldFlags() {
        return new String("flags");
    }
}

