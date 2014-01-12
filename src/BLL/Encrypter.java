package BLL;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Encryption algorithm used for user password
 * @author James Staite
 */
public class Encrypter 
{
   
    private static MessageDigest md;
    
    /**
     * Converts the passed string into a MD5 encryption
     * @param pass string to be encrypted
     * @return a string that represents the encryption
     */
    public static String encrypt(String pass)
    {
        try {
            // get encryptor
            md = MessageDigest.getInstance("MD5");
            
            // convert string into an array of bytes
            byte[] passBytes = pass.getBytes();
            
            // reset digest 
            md.reset();
            
            // process the byte array
            byte[] digested = md.digest(passBytes);
            
            // convert the digested byte array back into a string
            StringBuilder sb = new StringBuilder();
            for(int i=0;i<digested.length;i++){
                sb.append(Integer.toHexString(0xff & digested[i]));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Encrypter.class.getName()).log(Level.SEVERE, null, ex);
        }
            return null;
    }
}
