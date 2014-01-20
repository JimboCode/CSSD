package BLL;

/**
 * Defines the work flow for a media asset though the system by providing:-
 * a list of permitted actions based upon the submitted status
 * a list of worker types and roles to allocate each task to based upon the status
 * 
 * @author James Staite
 */
public class MediaAssetWorkFlow extends MediaItemWorkFlow
{
    @Override
    public MediaStatus[] getValidStatusOptions(MediaStatus status, boolean withAFile, MediaSource mediaSource, Worker worker) 
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
            
            case REQUESTED_FROM_CLIENT: case REPLACEMENT_REQUESTED_FROM_CLIENT:
            {
                if (worker.getWorkerType() == WorkerType.CLIENT) return new MediaStatus[] {MediaStatus.ARRIVED_IN_VAULT};
                else return new MediaStatus[] {};
            }
            
            case ORDERED_FROM_CONTRACTOR: case REORDERED_FROM_CONTRACTOR:
            {
                if (worker.getWorkerType() == WorkerType.CONTRACTOR) return new MediaStatus[] {MediaStatus.ARRIVED_IN_VAULT};
                else return new MediaStatus[] {};
            }
            
            case ORDERED_IN_HOUSE:
            {
                if (worker.getRole() == WorkerRoles.AUTHOR) return new MediaStatus[] {MediaStatus.ARRIVED_IN_VAULT};
                else return new MediaStatus[] {};
            }
                
            case ARRIVED_IN_VAULT:
            {
                if (worker.getWorkerType() == WorkerType.CLIENT ||
                        worker.getWorkerType() == WorkerType.CONTRACTOR ||
                        worker.getRole() == WorkerRoles.AUTHOR && withAFile) return new MediaStatus[] {MediaStatus.INWARD_QC};
                else return new MediaStatus[]{};
            }
            case INWARD_QC: case AWAITING_QC:
            {
                if (worker.getRole() == WorkerRoles.QC_TEAM_LEADER) return new MediaStatus[] {MediaStatus.QC_REPORT_AVALIABLE};
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
                
            case FIXES_ORDERED_DELAYED:
            {
                if (worker.getRole() == WorkerRoles.AUTHOR && withAFile) return new MediaStatus[] {MediaStatus.FIXES_COMPLETED};
                else return new MediaStatus[] {};
            }
                
            case FIXES_COMPLETED:
            {
                if (worker.getRole() == WorkerRoles.QC_TEAM_LEADER) return new MediaStatus[] {MediaStatus.AWAITING_QC};
                else return new MediaStatus[] {};
            }
                
            case APPROVED_FOR_COMPRESSION:
            {
                if (worker.getRole() == WorkerRoles.QC_TEAM_LEADER) return new MediaStatus[] {MediaStatus.COMPRESSION_COMPLETED};
                else return new MediaStatus[] {};
            }
                
            case COMPRESSION_COMPLETED:
            {
                if (worker.getRole() == WorkerRoles.AUTHOR && withAFile) return new MediaStatus[] {MediaStatus.AWAITING_QC};
                else return new MediaStatus[] {};
            }
                
            case ASSET_READY:
            {
                if (worker.getRole() == WorkerRoles.QC_TEAM_LEADER) 
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
    
    @Override
    public WorkerRoles[] getValidAllocateToWorkerRoles(MediaStatus status)
    {
        switch (status)
        {
            case AWAITING_ACTION:
            {
                return new WorkerRoles[]{WorkerRoles.PROJECT_MANAGER};
            }
            
            case ORDERED_IN_HOUSE: case ARRIVED_IN_VAULT: case FIXES_ORDERED_DELAYED:
            {
                return new WorkerRoles[]{WorkerRoles.AUTHOR, WorkerRoles.INTERRUPTER};
            }
            
            case APPROVED_FOR_COMPRESSION:
            {
                return new WorkerRoles[]{WorkerRoles.AUTHOR};                
            }
                
            case INWARD_QC: case AWAITING_QC:
            {
                return new WorkerRoles[]{WorkerRoles.QC};
            }
            case QC_REPORT_AVALIABLE: case FIXES_COMPLETED: case COMPRESSION_COMPLETED: case ASSET_READY:
            {
                return new WorkerRoles[]{WorkerRoles.QC_TEAM_LEADER};
            }
            case REQUESTED_FROM_CLIENT: case REPLACEMENT_REQUESTED_FROM_CLIENT:
            {
                return new WorkerRoles[]{WorkerRoles.CLIENT};
            }
        
        case ORDERED_FROM_CONTRACTOR: case REORDERED_FROM_CONTRACTOR:
            {
                return new WorkerRoles[]{WorkerRoles.CONTRACTOR};
            }
                        
            default:
            {
                return new WorkerRoles[]{};
            }
        }        
    }

    @Override
    public boolean getFileRequiredWithStatus(MediaStatus status) 
    {
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
