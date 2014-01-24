package BLL;

/**
 * Defines the work flow for a media element though the system by providing:-
 * a list of permitted actions based upon the submitted status
 * a list of worker types and roles to allocate each task to based upon the status
 * 
 * @author James Staite
 */
public class MediaElementWorkFlow extends MediaItemWorkFlow
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
    @Override
    public MediaStatus[] getsValidStatusOptions(MediaStatus status, MediaSource mediaSource, Worker worker) 
    {
        switch (status)
        {
            case ALL_ASSETS_AVALIABLE:
            {
                if(worker.getRole() == WorkerRoles.QC_TEAM_LEADER)
                {
                    return new MediaStatus[] {MediaStatus.QUICK_REQUESTED};
                }
                else return new MediaStatus[]{};
            }
                            
            case QC_REPORT_AVALIABLE:
            {
                if(worker.getRole() == WorkerRoles.QC_TEAM_LEADER) return new MediaStatus[] {MediaStatus.ASSET_READY, MediaStatus.AWAITING_ASSETS_DELAYED, MediaStatus.APPROVED_FOR_FINAL_COMPRESSION, MediaStatus.BUILD_COMPLETE};
                else return new MediaStatus[]{};
            }
                
            case ASSET_READY:
            {
                if(worker.getRole() == WorkerRoles.QC_TEAM_LEADER) return new MediaStatus[] {MediaStatus.APPROVED_FOR_FINAL_COMPRESSION, MediaStatus.AWAITING_ASSETS_DELAYED};
                else return new MediaStatus[]{};
            }
                
            default:
            {
                return new MediaStatus[]{};
            }
        }
    }
    
    /**
     * Provides an array of worker roles that a media status can be allocated to
     * @param status the status that the roles are required for
     * @return an array of WorkerRoles[] that are valid allocations for the status provided
     */
    @Override
    public WorkerRoles[] getValidAllocateToWorkerRoles(MediaStatus status) 
    {
        switch (status)
        {
            case ALL_ASSETS_AVALIABLE: case QUICK_CREATED: case QC_REPORT_AVALIABLE: case COMPRESSION_COMPLETED:
            {
                return new WorkerRoles[]{WorkerRoles.QC_TEAM_LEADER};
            }
            case QUICK_REQUESTED: case APPROVED_FOR_FINAL_COMPRESSION:
            {
                return new WorkerRoles[]{WorkerRoles.AUTHOR};
            }               
            case AWAITING_QC:
            {
                return new WorkerRoles[]{WorkerRoles.QC};
            }  
            case ASSET_READY:
            {
                return new WorkerRoles[]{WorkerRoles.PROJECT_MANAGER};
            }
                            
            default:
            {
                return new WorkerRoles[]{};
            }
        }
    }
}
