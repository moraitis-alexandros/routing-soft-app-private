package org.routing.software.security;

import org.mindrot.jbcrypt.BCrypt;

public class SecUtil {

    private SecUtil(){ }

    public static String hashPassword(String inputPwd) {
        int workload = 12; //default
        String salt = BCrypt.gensalt(workload); //with salt we avoid two users with same password have the same hashed password
        return BCrypt.hashpw(inputPwd, salt);
    }

    public static boolean checkPassword(String inputPwd, String storedHashedPwd) {
        return BCrypt.checkpw(inputPwd, storedHashedPwd); //attention first inputPwd then storedPwd, NOT vice versa
    }
}
