package BLL;

/**
 *
 * @author James Staite
 */
public abstract class MediaItemWorkFlow 
{
    /**
     * Returns a valid list of MediaStatus options based upon the Media Items current state and status
     * that the status could be changed to
     * @param withAFile
     * @param role
     * @return 
     */
    public abstract MediaStatus[] getValidStatusOptions(MediaStatus status, boolean withAFile, MediaSource mediaSource, Worker worker);
    
    public abstract WorkerRoles[] getValidAllocateToWorkerRoles(MediaStatus status);
}
