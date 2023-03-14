
public class Cesar {
    public Cesar() {
    }

    public String cesarEncrypt(String planeText){

        int key = 1, count = 0, i = 0;

        for(int j = 0; j < planeText.length(); j++) {
            count++;
        }

        char[] code = new char[count];


        char[] chars = planeText.toCharArray();

        for(char c : chars){
            if(c == (char)32){
                code[i] += (char)32;
            }else{
                c += key;
                code[i] = c;
            }
            i++;
        }



        return String.valueOf(code);

    }

    public String cesarDecrypt(String encryptText){

        int key = 1, count = 0, x = 0;

        for(int i = 0; i < encryptText.length(); i++) {
            count++;
        }

        char[] decode = new char[count];


        char[] chars = encryptText.toCharArray();

        for(char c2 : chars){
            if(c2 == (char)32){
                decode[x] += (char)32;
            }else{
                c2 -= key;
                decode[x] = c2;
            }
            x++;
        }

        return String.valueOf(decode);
    }

}