

import java.util.Scanner;

public class RleProgram {
    public static void main(String[] args) {

        Scanner scnr = new Scanner(System.in);
        boolean menuScreen = true;
        String stringData;
        byte[] imageData = null;
        //task 1 Display welcome message

        //task 2 display color test
        System.out.println("Welcome to the RLE image encoder!" + "\n");
        System.out.println("Displaying Spectrum Image:");

       ConsoleGfx.displayImage (ConsoleGfx.testRainbow); //Displaying Rainbow



        while(menuScreen) { //Start while loop to continuously display menu

            System.out.println("\n" + "RLE Menu");
            System.out.println("--------");
            System.out.println("0. Exit");
            System.out.println("1. Load File");
            System.out.println("2. Load Test Image");
            System.out.println("3. Read RLE String");
            System.out.println("4. Read RLE Hex String");
            System.out.println("5. Read Data Hex String");
            System.out.println("6. Display Image");
            System.out.println("7. Display RLE String");
            System.out.println("8. Display Hex RLE Data");
            System.out.println("9. Display Hex Flat Data" + "\n");

            System.out.print("Select a Menu Option: ");
            int menuOption = scnr.nextInt();


            switch (menuOption){

                case 0:                                                //Exit Menu Option
                    menuScreen = false;
                    break;

                case 1:                                                // Option 1:Getting the file name from the user
                    System.out.print("Enter name of file to load: ");
                    String fileName = scnr.next();
                    imageData= ConsoleGfx.loadFile(fileName); //Loading user input into load file
                    break;

                case 2:                                                //Option 2: Loading test image
                    System.out.println("Test image data loaded.");
                    imageData = ConsoleGfx.testImage;
                    break;

                case 3:                                                //Option 3: Entering RLE to be decoded to flat data
                    System.out.print("Enter an RLE string to be decoded: ");
                    stringData = scnr.next();
                    imageData = decodeRle(stringToRle(stringData));
                    break;

                case 4:                                                //Option 4: Converting RLE hex data to flat data
                    System.out.print("Enter the hex string holding RLE data: ");
                    stringData = scnr.next();
                    imageData = decodeRle(stringToData(stringData));
                    break;

                case 5:                                                // Option 5: Convert flat hex data to flat data
                    System.out.print("Enter the hex string holding flat data: ");
                    stringData= scnr.next();
                    imageData = stringToData(stringData);
                    break;

                case 6:                                                // Option 6: Displaying image of current flat data
                    System.out.println("Displaying image...");
                    ConsoleGfx.displayImage(imageData);
                    break;

                case 7:                                                //Option 7: Output RLE with ':'
                    System.out.print("RLE representation: ");
                    System.out.println(toRleString(encodeRle(imageData)));
                    break;

                case 8:                                                //Option 8: Output RLE hex data without ':'
                    System.out.print("RLE hex values: ");
                    System.out.println(toHexString(encodeRle(imageData)));
                    break;

                case 9:                                                //Option 9: output flat data as hex data
                    System.out.println("Flat hex values: " + toHexString(imageData));
                    break;
                default:
                    System.out.println("Invalid Input. Please enter a value between 0 and 9");
                    break;

            }

        }
    }

    //Method 1: Convert flat data to hex string
    public static String toHexString(byte[] data){
        char[] hexVal = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        StringBuilder hexString = new StringBuilder();
        char convertChar;

        for (byte datum : data) {
            convertChar = hexVal[datum];
            hexString.append(convertChar);
        }

        return String.valueOf(hexString);

    }

    //Method 2: Counting number of occurrences of given pixel value {15,15,15,4,4,4,4,4,4} array of 9 elements. Want to create and return a new array of {3,15,6,4}
    public static int countRuns(byte[] flatData){
        int count = 1;
        int numOfSequences = 1;

        for (int i = 0; i < flatData.length - 1; i++) {
            if (flatData[i] == flatData[i + 1]) {
                count++;
                if(count > 15){
                    numOfSequences++;
                    count =1;

                }

            } else if (flatData[i] != flatData[i + 1])  {
                count = 1;
                numOfSequences++;
            }

        }

        return numOfSequences;
    }

    //Method 3: encode flat data to RLE

    public static byte[] encodeRle(byte[] flatData){

        byte count = 1;
        byte index = 0;
        byte valOfi = 1;
        int runCount = countRuns(flatData); //Called countRuns method
        runCount *= 2;
        byte[] arr = new byte[runCount];
        for (int i = 0; i < flatData.length-1; i++) {
            valOfi = flatData[i];

            if (flatData[i] == flatData[i + 1]) {
                count++;

                if (count>15){
                    count = 15;
                    arr[index++] = count;
                    arr[index++] = valOfi;
                    count =1;
                }

            }
            else if (flatData[i] != flatData[i + 1]) {
                arr[index++] =  count; //assign index with number of times value was repeate
                arr[index++] = valOfi;
                valOfi = flatData[i+1];
                count = 1;
            }

        }
        arr[index++] = count;
        arr[index] = valOfi;
        return arr;

    }
    /* Method 4: Return the sum of even indices.
    It flattens the array, so {3,15,6,4} means 15 shows up 3 times and 4 shows up 6 times. This return a value of 9 since 3+6 =9.
    Simply asks how long the flattened array will be. So just need to take sum of values at even indices.
     */
    public static int getDecodedLength(byte[] rleData){

        int lenOfFlattenedArray = 0;

        for(int i = 0; i < rleData.length; i++){

            if (i % 2 == 0)
                lenOfFlattenedArray += rleData[i];

        }

        return lenOfFlattenedArray;

    }

    //Method 5: Decode RLE data to flat data. Basically want to expand the original array

    public static byte[] decodeRle(byte[] rleData){
        int size = 0;
        int index = 0; //Keeps track of index of 'res' that is being assigned a value

        for (int i =0; i < rleData.length; i++){
            if(i % 2 == 0)
                size += rleData[i];
        }

        byte [] res = new byte[size];
        int repeats;
        int value;

        for (int i = 0; i < rleData.length; i += 2) { //keeping i even by  doing i+=2

                value = rleData[i + 1];
                repeats = rleData[i];
                if(repeats > 15){
                    repeats =1;
                }

            for(int j = 1; j <= repeats; j++){
                res[index] += value;
                index++; //the index is incremented after each loop so that the next index gets the next value

            }
        }
        return res;

    }
    //Method 6: convert string of hex data to flat data.
    public static byte[] stringToData(String dataString ){

        int size = 0;
        int index = 0; //Keeps track of index of 'res' that is being assigned a value

        for (int i =0; i < dataString.length(); i++){
            size ++;
        }

        byte [] res = new byte [size];
        for(int i = 0; i< dataString.length(); i++) {
            int value;
            if (Character.isDigit(dataString.charAt(i))) {

                value = Integer.parseInt(Character.toString(dataString.charAt(i)));
                res[index] = Byte.parseByte(String.valueOf(value));

            }
            else if (Character.isLetter(dataString.charAt(i))) {

               int num = Character.getNumericValue(dataString.charAt(i));
                res[index] = (byte) num;
            }

            index++;

        }
        return res;
    }
    //Method 7: Translate  RLE  data  into  a  human-readable  representation.
    public static String toRleString(byte[] rleData) {


        StringBuilder temprlestring = new StringBuilder();
        int i;

        for(i=0; i<rleData.length; i+=2){
            if(i != 0){
                temprlestring.append(':');
            }

            temprlestring.append(rleData[i]).append(toHexString(new byte[]{rleData[i + 1]})); //Called toHextring method to convert hexadecimal numbers
        }

        return temprlestring.toString();
    }

    //Method 8: Translate a string in human-readable RLE format (with delimiters) into RLE byte data.
    public static byte[] stringToRle(String rleString){

        String [] removal = rleString.split(":"); //Splitting string at each occurrence of ":"
        byte [] arr = new byte[removal.length * 2];
        char hex;

        for( int i = 0; i < removal.length; i++) {

            arr[i*2] = Byte.parseByte(removal[i].substring(0, removal[i].length() - 1));

            hex = removal[i].charAt(removal[i].length() - 1);

            if (hex <= 9) {
                arr[i*2+1] = (byte) hex;
            } else {
                arr[i*2+1] = (byte) Integer.parseInt(String.valueOf(hex),16);
            }
        }

        return arr;

    }

    
}
