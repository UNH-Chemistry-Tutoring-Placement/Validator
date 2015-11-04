

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class StudentFile {

    private final String formatKey = "Class Info Format";
    private final String descriptionKey = "Description";
    private final String numStudentKey = "Number of solution";

    private final String nameKey = "Name";
    private final String emailKey = "Email";
    private final String lectureKey = "Lecture";
    private final String yearKey = "Year";
    private final String sexKey = "Sex";

    private final String goodTimeKey = "Number of good times";
    private final String possibleTimeKey = "Number of possible times";

    private double version, numberOfStudents;
    private String description;
    private HashMap<String,HashMap<String,String>> studentInfo;
    private HashMap<String,ArrayList<String>> studentPossibleTimes;
    private HashMap<String,ArrayList<String>> studentGoodTimes;


    public StudentFile( File file ){
        studentInfo = new HashMap<>();
        studentPossibleTimes = new HashMap<>();
        studentGoodTimes = new HashMap<>();
        parseStudentFile(file);
    }

    private void parseStudentFile( File file ){

        try {
            Scanner studentScanner = new Scanner(file);
            String currentStudent = "";
            while( studentScanner.hasNextLine() ){
                String nextLine = studentScanner.nextLine();
                if( nextLine.startsWith("#"))
                    continue;
                Scanner lineScanner = new Scanner( nextLine ).useDelimiter(":");
                if( !lineScanner.hasNext() ){
                    if( studentScanner.hasNextLine())
                        nextLine = studentScanner.nextLine();
                    else
                        return;
                    lineScanner = new Scanner( nextLine ).useDelimiter(":");
                }
                String nextToken = lineScanner.next();
                switch (nextToken){
                    case formatKey:
                        version = Double.parseDouble(lineScanner.next().trim());
                        break;
                    case descriptionKey:
                        description = lineScanner.next().trim();
                        break;
                    case numStudentKey:
                        numberOfStudents = Integer.parseInt(lineScanner.next().trim());
                        break;
                    case nameKey:
                        currentStudent = lineScanner.next().trim();
                        studentInfo.put(currentStudent, new HashMap<>());
                        break;
                    case emailKey:
                        studentInfo.get(currentStudent).put(emailKey,lineScanner.next().trim());
                        break;
                    case lectureKey:
                        studentInfo.get(currentStudent).put(lectureKey,lineScanner.next().trim());
                        break;
                    case yearKey:
                        studentInfo.get(currentStudent).put( yearKey, lineScanner.next().trim());
                        break;
                    case sexKey:
                        studentInfo.get( currentStudent ).put( sexKey, lineScanner.next().trim() );
                        break;
                    case goodTimeKey:
                        int numGoodTimes = Integer.parseInt(lineScanner.next().trim());
                        studentGoodTimes.put( currentStudent, new ArrayList<>() );
                        for( int i = 0; i < numGoodTimes; i++ ){
                            String nextTime = studentScanner.nextLine().trim();
                            studentGoodTimes.get( currentStudent ).add( nextTime );
                        }
                        break;
                    case possibleTimeKey:
                        int numPossibleTimes = Integer.parseInt(lineScanner.next().trim());
                        studentPossibleTimes.put( currentStudent, new ArrayList<>() );
                        for( int i = 0; i < numPossibleTimes; i++ ){
                            String nextTime = studentScanner.nextLine();
                            studentPossibleTimes.get( currentStudent ).add( nextTime.trim() );
                        }
                        break;
                }
            }

        } catch (FileNotFoundException e ){
            System.err.println( "File " + file.getName() + " not found.");
        }
    }


    public String printAllStudentInfo( String name ){

        if( !studentInfo.containsKey(name) ){
            System.err.println( name + " not found in records.");
            return "";
        }

        StringBuilder studentInfo = new StringBuilder();
        studentInfo.append(name).append('\n');
        studentInfo.append(getStudentEmail(name)).append('\n');
        studentInfo.append(getStudentLectureTime(name)).append('\n');
        studentInfo.append(getStudentYear(name)).append('\n');
        studentInfo.append(getStudentSex(name)).append('\n');
        studentInfo.append(getStudentNumGoodTimes(name)).append('\n');
        studentInfo.append(getGoodTimesByName(name).toString()).append('\n');
        studentInfo.append(getStudentNumPossibleTimes(name)).append('\n');
        studentInfo.append( getPossibleTimesByName(name).toString()).append('\n');

        return studentInfo.toString();
    }

    public String getStudentEmail( String name ){

        if( studentInfo.containsKey(name) )
            return studentInfo.get(name).get(emailKey);
        System.err.println( name + " not found in records.");
        return "";
    }

    public String getStudentLectureTime( String name ){
        if( studentInfo.containsKey(name))
            return studentInfo.get(name).get(lectureKey);
        System.err.println( name + " not found in records.");
        return "";
    }

    public String getStudentYear( String name ){
        if( studentInfo.containsKey(name))
            return studentInfo.get(name).get(yearKey);
        System.err.println( name + " not found in records.");
        return "";
    }

    public String getStudentSex( String name ){
        if( studentInfo.containsKey(name))
            return studentInfo.get(name).get(sexKey);
        System.err.println( name + " not found in records.");
        return "";
    }

    public int getStudentNumGoodTimes(String name){
        if( studentInfo.containsKey(name))
            return studentGoodTimes.get(name).size();
        System.err.println( name + " not found in records.");
        return -1;
    }

    public int getStudentNumPossibleTimes( String name ){
        if( studentInfo.containsKey(name))
            return studentPossibleTimes.get(name).size();
        System.err.println( name + " not found in records.");
        return -1;
    }

    public ArrayList<String> getPossibleTimesByName( String name ){
        if( studentPossibleTimes.containsKey(name))
            return studentPossibleTimes.get(name);
        System.err.println( name + " not found in records.");
        return new ArrayList<>();
    }

    public ArrayList<String> getGoodTimesByName( String name ){
        if( studentGoodTimes.containsKey(name))
            return studentGoodTimes.get(name);
        System.err.println( name + " not found in records.");
        return new ArrayList<>();

    }

    public static void main( String[] args ){
        System.out.println("\n" + new StudentFile(new File(args[0])).printAllStudentInfo("Riley McDonald"));
    }

}
