package BLL;

/**
 * Defines the work flow for a media asset though the system by providing:-
 * a list of permitted actions based upon the submitted status
 * a list of worker types and roles to allocate each task to based upon the status
 * 
 * @author James Staite
 */
public class MediaAssetSubtitlesWorkFlow extends MediaItemWorkFlow
{
    /**
     * Implements the MediaItemWorkFlow interface and provides valid status changes for the given parameters
     * @param status current status of the mediaItem
     * @param mediaSource the media source of the mediaItem
     * @param worker the worker requesting the change
     * @return a MediaStatus[] array of valid options
     */
    @Override
    public MediaStatus[] getsValidStatusOptions(MediaStatus status, MediaSource mediaSource, Worker worker) 
    {
        switch (status)
        {
            case AWAITING_ACTION:
            {
                if(worker.getRole() == WorkerRoles.PROJECT_MANAGER)
                {
                    switch (mediaSource)
                    {
                        case CLIENT:
                        {
                            return new MediaStatus[] {MediaStatus.REQUESTED_FROM_CLIENT};
                        }
                        case SUBCONTRACTOR:
                        {
                            return new MediaStatus[] {MediaStatus.ORDERED_FROM_CONTRACTOR};
                        }
                        case IN_HOUSE:
                        {
                            return new MediaStatus[] {MediaStatus.ORDERED_IN_HOUSE};
                        }
                        default:
                        {
                            return new MediaStatus[]{};
                        }
                    }
                }
                else return new MediaStatus[] {};
            }
            
            case QC_REPORT_AVALIABLE:
            {
                if (worker.getRole() == WorkerRoles.QC_TEAM_LEADER)
                {                    
                    switch (mediaSource)
                    {
                        case CLIENT:
                        {
                            return new MediaStatus[] {MediaStatus.REPLACEMENT_REQUESTED_FROM_CLIENT, MediaStatus.FIXES_ORDERED_DELAYED, MediaStatus.APPROVED_FOR_COMPRESSION, MediaStatus.ASSET_READY};
                        }
                        case SUBCONTRACTOR:
                        {
                            return new MediaStatus[] {MediaStatus.REORDERED_FROM_CONTRACTOR, MediaStatus.FIXES_ORDERED_DELAYED, MediaStatus.APPROVED_FOR_COMPRESSION, MediaStatus.ASSET_READY};
                        }
                        case IN_HOUSE:
                        {
                            return new MediaStatus[] {MediaStatus.FIXES_ORDERED_DELAYED, MediaStatus.APPROVED_FOR_COMPRESSION, MediaStatus.ASSET_READY};
                        }
                    }
                }
                else return new MediaStatus[] {};
            }
                
            case ASSET_READY:
            {
                if (worker.getRole() == WorkerRoles.PROJECT_MANAGER) 
                {
                    switch (mediaSource)
                    {
                        case CLIENT:
                        {
                            return new MediaStatus[] {MediaStatus.REPLACEMENT_REQUESTED_FROM_CLIENT, MediaStatus.FIXES_ORDERED_DELAYED, MediaStatus.APPROVED_FOR_COMPRESSION};
                        }
                        case SUBCONTRACTOR:
                        {
                            return new MediaStatus[] {MediaStatus.REORDERED_FROM_CONTRACTOR, MediaStatus.FIXES_ORDERED_DELAYED, MediaStatus.APPROVED_FOR_COMPRESSION};
                        }
                        case IN_HOUSE:
                        {
                            return new MediaStatus[] {MediaStatus.FIXES_ORDERED_DELAYED, MediaStatus.APPROVED_FOR_COMPRESSION};
                        }
                    }
                }
                else return new MediaStatus[] {};
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
            case ORDERED_IN_HOUSE: case ARRIVED_IN_VAULT: case FIXES_ORDERED_DELAYED:
            {
                return new WorkerRoles[]{WorkerRoles.INTERRUPTER};
            }
                
            case REQUESTED_FROM_CLIENT: case REPLACEMENT_REQUESTED_FROM_CLIENT:
            {
                return new WorkerRoles[]{WorkerRoles.CLIENT};
            }
        
            case ORDERED_FROM_CONTRACTOR: case REORDERED_FROM_CONTRACTOR:
            {
                return new WorkerRoles[]{WorkerRoles.CONTRACTOR};
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
