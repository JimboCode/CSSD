package BLL;

/**
 *
 * @author James
 */
public class MediaElementWorkFlow extends MediaItemWorkFlow
{
    @Override
    public MediaStatus[] getValidStatusOptions(MediaStatus status, boolean withAFile, MediaSource mediaSource, Worker worker) 
    {
        switch (status)
        {
            case AWAITING_ASSETS:
            {
                if(worker == null)
                {
                    return new MediaStatus[] {MediaStatus.ALL_ASSETS_AVALIABLE, MediaStatus.AWAITING_ASSETS_DELAYED};
                }
                else return new MediaStatus[]{};
            }
            
            case AWAITING_ASSETS_DELAYED:
            {
                if(worker == null)
                {
                    return new MediaStatus[] {MediaStatus.ALL_ASSETS_AVALIABLE};
                }
                else return new MediaStatus[]{};
            }
                
            case ALL_ASSETS_AVALIABLE:
            {
                if(worker.getRole() == WorkerRoles.QC_TEAM_LEADER)
                {
                    return new MediaStatus[] {MediaStatus.QUICK_REQUESTED};
                }
                else return new MediaStatus[]{};
            }
            case QUICK_REQUESTED:
            {
                if (worker.getRole() == WorkerRoles.AUTHOR && withAFile) return new MediaStatus[] {MediaStatus.QUICK_CREATED};
                else return new MediaStatus[]{};
            }
                
            case QUICK_CREATED:
            {
                if(worker.getRole() == WorkerRoles.QC_TEAM_LEADER) return new MediaStatus[] {MediaStatus.AWAITING_QC};
                else return new MediaStatus[]{};
            }
                
            case AWAITING_QC:
            {
                if(worker.getRole() == WorkerRoles.QC) return new MediaStatus[] {MediaStatus.QC_REPORT_AVALIABLE};
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
                
            case APPROVED_FOR_FINAL_COMPRESSION:
            {
                if (worker.getRole() == WorkerRoles.AUTHOR) return new MediaStatus[] {MediaStatus.COMPRESSION_COMPLETED};
                else return new MediaStatus[]{};
            }
            
            case COMPRESSION_COMPLETED:
            {
                if(worker.getRole() == WorkerRoles.QC_TEAM_LEADER) return new MediaStatus[] {MediaStatus.AWAITING_QC};
                else return new MediaStatus[]{};
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
            case AWAITING_ASSETS:
            {
                return new WorkerRoles[]{};
            }
            
            case AWAITING_ASSETS_DELAYED:
            {
                return new WorkerRoles[]{};
            }
                
            case ALL_ASSETS_AVALIABLE: case QUICK_CREATED: case QC_REPORT_AVALIABLE: case ASSET_READY: case COMPRESSION_COMPLETED:
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
                            
            default:
            {
                return new WorkerRoles[]{};
            }
        }
    }

    @Override
    public boolean getFileRequiredWithStatus(MediaStatus status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
