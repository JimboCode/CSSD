package BLL;

/**
 * Work flow interface for Assets and Element control of operations
 * 
 * @author James Staite
 */
public abstract class MediaItemWorkFlow 
{
    /**
     * Returns a valid list of valid MediaStatus options based upon the status of Media Item
     * current state; This interface handle some state which have fixed paths; Provides delegated
     * calls to the specialist class for the following states
     *      AWAITING_ACTION
     *      QC_REPORT_AVALIABLE
     *      ASSET_READY
     *      ALL_ASSETS_AVALIABLE
     * 
     * @param status The current status of the MediaItem
     * @param mediaSource The source of media for the MediaItem
     * @param worker The user intending to make the change
     * @return An array of valid MediaStatus changes
     */
    public MediaStatus[] getHandledValidStatusOptions(MediaStatus status, MediaSource mediaSource, Worker worker)
    {
        switch (status)
        {    
            case ARRIVED_IN_VAULT:
            {
                if (worker.getRole() == WorkerRoles.PROJECT_MANAGER ||
                        worker.getRole() == WorkerRoles.QC_TEAM_LEADER) return new MediaStatus[] {MediaStatus.INWARD_QC};
                else return new MediaStatus[]{};
            }
            
            case FIXES_COMPLETED:
            {
                if (worker.getRole() == WorkerRoles.QC_TEAM_LEADER) return new MediaStatus[] {MediaStatus.AWAITING_QC};
                else return new MediaStatus[] {};
            }
                
            case COMPRESSION_COMPLETED:
            {
                if (worker.getRole() == WorkerRoles.QC_TEAM_LEADER) return new MediaStatus[] {MediaStatus.AWAITING_QC};
                    else return new MediaStatus[] {};
            }
                
            case QUICK_CREATED:
                if (worker.getRole() == WorkerRoles.QC_TEAM_LEADER) return new MediaStatus[] {MediaStatus.AWAITING_QC};
                    else return new MediaStatus[] {};
            
            default:
            {
                // if not handled delegate to specialisted class
                return getsValidStatusOptions(status, mediaSource, worker);
            }
        }
    }
    
    /**
     * Delegated call to the a specialised class to handle status changes not covered in the interface
     * 
     * @param status The current status of the MediaItem
     * @param mediaSource The source of media for the MediaItem
     * @param worker The user intending to make the change
     * @return An array of valid MediaStatus changes
     */
    public abstract MediaStatus[] getsValidStatusOptions(MediaStatus status, MediaSource mediaSource, Worker worker);
    
    /**
     * Returns a valid list of valid of WorkerRoles based upon the status of Media Item
     * current state; This interface handles some state which have fixed paths; Provides a delegate
     * call to the specialist classes for other options
     * @param status The current status of the MediaItem
     * @return An array of valid WorkRoles for the passed status
     */
    public WorkerRoles[] getHandledValidAllocateToWorkerRoles(MediaStatus status)
    {
        if (status == null) return new WorkerRoles[]{};
        
        switch (status)
        {
            case AWAITING_ACTION: case ASSET_READY:
            {
                return new WorkerRoles[]{WorkerRoles.PROJECT_MANAGER};
            }
            
            case APPROVED_FOR_COMPRESSION: case QUICK_REQUESTED: case APPROVED_FOR_FINAL_COMPRESSION:
            {
                return new WorkerRoles[]{WorkerRoles.AUTHOR};                
            }
                
            case INWARD_QC: case AWAITING_QC:
            {
                return new WorkerRoles[]{WorkerRoles.QC};
            }
            
            case QC_REPORT_AVALIABLE: case FIXES_COMPLETED: case COMPRESSION_COMPLETED:
            {
                return new WorkerRoles[]{WorkerRoles.QC_TEAM_LEADER};
            }
                
            default:
            {
                // call specialised class if status is not handled
                return getValidAllocateToWorkerRoles(status);
            }
        }        
    }
    
    /**
     * Delegated call to the a specialised class to handle status options not covered in the interface
     * @param status The current status of the MediaItem
     * @return An array of valid WorkRoles for the passed status
     */
    public abstract WorkerRoles[] getValidAllocateToWorkerRoles(MediaStatus status);
    
    /** 
     * Confirm if the status operation requires a file to be submitted as part of the status change
     * @param status the status being required
     * @return true - file is required / false - no file required
     */
    public boolean getFileRequiredWithStatus(MediaStatus status)
    {
        // check that null has not been passed in
        if (status == null) return false;
        
        // handle status request
        switch (status)
        {
            case ORDERED_IN_HOUSE:
            case REQUESTED_FROM_CLIENT:
            case FIXES_ORDERED_DELAYED:
            case REPLACEMENT_REQUESTED_FROM_CLIENT:
            case ORDERED_FROM_CONTRACTOR:
            case REORDERED_FROM_CONTRACTOR:
            {
                return true;
            }
            default:
            {
                return false;
            }
        }       
    }
}
