package tdtu.edu.vigerene.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tdtu.edu.vigerene.Entity.User;
import tdtu.edu.vigerene.Repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;

    public User createUser(User user){
        return userRepo.save(user);
    }

    public User updateUser(int id, User updatedUser){
        User oldUser = userRepo.findById(id);
        if (oldUser != null) {
            oldUser.setUsername(updatedUser.getUsername());
            oldUser.setPassword(updatedUser.getPassword());
            oldUser.setVigenerekey(updatedUser.getVigenerekey());
            return userRepo.save(oldUser);
        } else {
            return null;
        }
    }

//    public List<User> getAllUser(){
//        return userRepo.findAll();
//    }

    public boolean existsByUsername(String username){
        User user = userRepo.findByUsername(username);
        return user != null;
    }

    private  int countDigits(String str) {
        int count = 0;

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);

            if (Character.isDigit(ch)) {
                count++;
            }
        }
        return count;
    }

    public boolean containsNonAlphanumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isLetter(c)) {
                return true;
            }
        }
        return false;
    }
    private  String autoKey(String str, String key) {
        if (key.length() >= str.length()) {
            return key.substring(0, str.length());
        }
        int digit_len = countDigits(str);
//        Neu do dai cua khoa be hon ban ro thi ta se them vao khoa cac ky tu cua ban ro cho den khi do dai khoa
//        bang voi ban ro (neu ban ro co cac ky tu dac biet hoac so thi khong them vao khoa)
        StringBuilder newKey = new StringBuilder(key);
        for (int i = 0; i < str.length(); i++) {
            if (newKey.length() == str.length() - digit_len) {
                break;
            }

            if(Character.isLetter(str.charAt(i))){
                newKey.append(str.charAt(i));
            }else{
                continue;
            }

        }
        return newKey.toString();
    }


    public  String encryptVigenere(String str, String key) {
        //Kiem tra neu key rong se tra ve van ban goc
        String encryptedString = "";
        if(key == ""){
            return str;
        }
        // Sinh khoa moi theo thuat toan autokey
        String newKey = autoKey(str, key);

        int i_key = 0;
        for (int i = 0; i < str.length(); i++) {
            char strChar = str.charAt(i);
            //Neu khong phai chu cai se giu nguyen ky tu va tiep tuc vong lap
            if(!Character.isAlphabetic(strChar)){
                encryptedString += strChar;
                continue;
            }
            char keyChar = Character.toLowerCase(newKey.charAt(i_key));
            if (Character.isUpperCase(strChar)) {
                //Ma hoa ky tu in hoa
                int encryptedChar = (strChar + keyChar - 2 * 'A') % 26 + 'A';
                encryptedString += (char) encryptedChar;
            } else if (Character.isLowerCase(strChar)) {
                //Ma hoa ky tu thuong
                int encryptedChar = (strChar + keyChar - 2 * 'a') % 26 + 'a';
                encryptedString += (char) encryptedChar;
            } else {
                encryptedString += strChar;
            }

            i_key ++;
        }
        return encryptedString;
    }


    public  String decryptVigenere(String str, String key) {
        String decryptedString = "";
        //Kiem tra neu key rong se tra ve van ban goc
        if(key == ""){
            return str;
        }
        int i_key = 0;
        for (int i = 0; i < str.length(); i++) {
            char strChar = str.charAt(i);
            //Neu khong phai chu cai se giu nguyen ky tu va tiep tuc vong lap
            if(!Character.isAlphabetic(strChar)){
                decryptedString += strChar;
                continue;
            }
            char keyChar =  Character.toLowerCase(key.charAt(i_key));

            if (Character.isUpperCase(strChar)) {
                //Giai ma ky tu in hoa va them ky tu vua giai ma vao key
                int decryptedChar = (strChar - keyChar + 26) % 26 + 'A';
                decryptedString += (char) decryptedChar;
                key += (char) decryptedChar;
            } else if (Character.isLowerCase(strChar)) {
                //Giai ma ky tu thuong va them ky tu vua giai ma vao key
                int decryptedChar = (strChar - keyChar + 26) % 26 + 'a';
                decryptedString += (char) decryptedChar;
                key += (char) decryptedChar;
            } else {
                decryptedString += strChar;
            }
            i_key ++;
        }
        return decryptedString;
    }

    public User authenticateUser(String username, String password){

        //Tim user bang username
        User user = userRepo.findByUsername(username);

        //Neu ton tai user voi mat khau da duoc ma hoa, thuc hien giai ma mat khau va xac thuc
        if(user != null && user.getVigenerekey() != null){
            String decryptedPassword = decryptVigenere(user.getPassword(), user.getVigenerekey());
            if(user.getUsername().equals(username) && password.equals(decryptedPassword))   {
                return user;
            }
            //Neu ton tai user voi mat khau chua duoc ma hoa thi chi can xac thuc mat khau hien co trong csdl
        }else if(user != null && user.getVigenerekey() == null){

            if(user.getUsername().equals(username) && user.getPassword().equals(password))   {
                return user;
            }
        }

        return null;
    }




}
