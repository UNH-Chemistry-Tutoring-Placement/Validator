import javafx.util.Pair;

import java.io.*;
import java.nio.Buffer;
import java.nio.file.Paths;
import java.util.*;

public class Validate {

    private ObjectiveFile objectiveFile;
    private StudentFile studentFile;
    private SolutionFile solutionFile;
    private String fullFile;

    private final String solutionHeader = "Solution Format";
    private final String objHeader = "Objective Function Format";
    private final String studentHeader = "Student Info Format";
    private final String classHeader = "Class Info Format";
    private boolean debug = true;

    /**
     * Init from system.in - cat'ed files
     */
    public Validate( ){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while ((line = reader.readLine()) != null) {
                if( !line.startsWith("#"))
                    fullFile += line + "\n";
            }
            String[] split1 = fullFile.split(classHeader);
            String[] split2 = split1[1].split(studentHeader);
            String[] split3 = split2[1].split(solutionHeader);

            String objectiveFormat = split1[0].replace("null", ""); // good
            String classInfo = classHeader + split2[ 0 ]; // good
            String studentInfo = studentHeader + split3[ 0 ]; // good
            String solution = solutionHeader + split3[ 1 ]; // good

            File objFile = File.createTempFile( "obj", "1", new File("."));
            // File classFile = File.createTempFile("class", "1", new File("."));
            File studFile = File.createTempFile("stud", "1", new File("."));
            File solFile = File.createTempFile("sol", "1", new File("."));

            FileWriter writer = new FileWriter(objFile);
            writer.write(objectiveFormat);
            writer.close();

            // CLASS FILE NOT USED YET
            // writer = new FileWriter(classFile);
            // writer.write(classInfo);
            // writer.close();

            writer = new FileWriter(studFile);
            writer.write(studentInfo);
            writer.close();

            writer = new FileWriter(solFile);
            writer.write(solution);
            writer.close();

            init(objFile.getName(), studFile.getName(), solFile.getName() );


        } catch (IOException e ){
            System.err.println( "IO Exception: " + e.getMessage());
        }
    }

    /**
     * Init from 3 different files
     * @param obj - objective file name
     * @param students - student file name
     * @param sol - solution file name
     */
    public Validate( String obj, String students, String sol ){
        init( obj, students, sol);

    }

    private void init( String obj, String students, String sol ){
        File objective = new File( obj );
        File student = new File( students );
        File solution = new File( sol );

        objectiveFile = new ObjectiveFile(objective);
        studentFile = new StudentFile(student);
        solutionFile = new SolutionFile(solution);

        objective.delete();
        student.delete();
        solution.delete();

        validate();
    }

    public void validate(){

        int penalty = 0;
        int aboveMaxPenalty = objectiveFile.getAboveMaxPenalty();
        int belowMinPenalty = objectiveFile.getBelowMinPenalty();
        int minGroupSize = objectiveFile.getMinGroupSize();
        int maxGroupSize = objectiveFile.getMaxGroupSize();
        int possibleChoicePenalty = objectiveFile.getPossibleChoicePenalty();
        int differentProfessorPenalty = objectiveFile.getDiffProfessorPenalty();
        int genderSoloPenalty = objectiveFile.getGenderSoloPenalty();

        HashMap<Pair<String,String>, ArrayList<String>> groups = solutionFile.getGroups();

        Iterator<Pair<String,String>> mapIter = groups.keySet().iterator();
        Pair<String,String> time;
        ArrayList<String> studentsInGroup;
        String professor = "";


        while( mapIter.hasNext() ){

            boolean difProfApplied = false;
            int femalesInGroup = 0;
            int malesInGroup = 0;
            time = mapIter.next();
            studentsInGroup = groups.get(time);

            if( studentsInGroup.size() > maxGroupSize ){
                penalty += (studentsInGroup.size() - maxGroupSize ) * aboveMaxPenalty;
                if( debug )
                    System.out.println( "Applied max group penalty");
            }
            if( studentsInGroup.size() < minGroupSize ) {
                penalty += (minGroupSize - studentsInGroup.size() ) * belowMinPenalty;
                if( debug )
                    System.out.println( "Applied min group penalty");
            }
            for( String studentName: studentsInGroup ){
                String studentSex = studentFile.getStudentSex(studentName);

                if( professor.equals(""))
                    professor = studentFile.getStudentProfessor(studentName);
                if( !difProfApplied && !professor.equals(studentFile.getStudentProfessor(studentName))) {
                    penalty += differentProfessorPenalty;
                    difProfApplied = true;
                    if( debug )
                        System.out.println( "Applied different professor penalty");
                }

                if( studentSex.equals( "Male" ))
                    malesInGroup++;
                if( studentSex.equals( "Female" ))
                    femalesInGroup++;

                if( studentFile.getPossibleTimes(studentName).contains(time.getKey()) ){
                    penalty += possibleChoicePenalty;
                    if( debug )
                        System.out.println( "Applied possible time penalty");
                }
            }

            if( (malesInGroup == 1 && femalesInGroup > 1)
                    || (malesInGroup > 1 && femalesInGroup == 1) ) {
                if( debug )
                    System.out.println( "Applied gender solo penalty");
                penalty += genderSoloPenalty;
            }

        }
        System.out.println("\nValidator Assigned Penalty: " + penalty );
        System.out.println("Solver assigned Penalty: " + solutionFile.getSolutionCost() + '\n');
    }

    public void printRosters(){

        HashMap<Pair<String,String>, ArrayList<String>> groups = solutionFile.getGroups();
        Iterator<Pair<String,String>> mapIter = groups.keySet().iterator();

        while( mapIter.hasNext() ){
            Pair<String,String> taEmailTime = mapIter.next();
            System.out.println( "TA email: " + taEmailTime.getValue() + " | Group Time: " + taEmailTime.getKey());
            System.out.println("--------------------------------------");
            for( String studentEmail: groups.get(taEmailTime) ){
                System.out.printf( "%-25s %-25s\n", studentFile.getStudentNameByEmail(studentEmail), studentEmail);
            }
            System.out.println();
        }
    }

    public static void main( String[] args ){

        Validate validate = new Validate();
        validate.printRosters();
    }


}
