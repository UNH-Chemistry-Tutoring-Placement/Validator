import javafx.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Validate {

    private ObjectiveFile objectiveFile;
    private StudentFile studentFile;
    private SolutionFile solutionFile;

    public Validate( String obj, String students, String sol ){

        File objective = new File( obj );
        File student = new File( students );
        File solution = new File( sol );

        objectiveFile = new ObjectiveFile(objective);
        studentFile = new StudentFile(student);
        solutionFile = new SolutionFile(solution);
        validate();
    }

    public void validate(){

        int penalty = 0;
        int aboveMaxPenalty = objectiveFile.getAboveMaxPenalty();
        int belowMinPenalty = objectiveFile.getBelowMinPenalty();
        int minGroupSize = objectiveFile.getMinGroupSize();
        int maxGroupSize = objectiveFile.getMaxGroupSize();
        int possibleChoicePenalty = objectiveFile.getPossibleChoicePenalty();
        HashMap<Pair<String,String>, ArrayList<String>> groups = solutionFile.getGroups();

        Iterator<Pair<String,String>> mapIter = groups.keySet().iterator();
        Pair<String,String> time;
        ArrayList<String> studentsInGroup;

        while( mapIter.hasNext() ){

            time = mapIter.next();
            studentsInGroup = groups.get(time);

            if( studentsInGroup.size() > maxGroupSize ){
                penalty += aboveMaxPenalty;
            }
            if( studentsInGroup.size() < minGroupSize ) {
                penalty += belowMinPenalty;
            }
            for( String studentName: studentsInGroup ){
                if( studentFile.getPossibleTimesByName(studentName).contains(time.getKey()) ){
                    penalty += possibleChoicePenalty;
                }
            }
        }
        System.out.println("Penalty: " + penalty + "\n");
    }

    public void printRosters(){
        HashMap<Pair<String,String>, ArrayList<String>> groups = solutionFile.getGroups();
        Iterator<Pair<String,String>> mapIter = groups.keySet().iterator();

        while( mapIter.hasNext() ){
            Pair<String,String> taEmailTime = mapIter.next();
            System.out.println( "TA email: " + taEmailTime.getValue() + " | Group Time: " + taEmailTime.getKey());
            System.out.println("--------------------------------------");
            for( String studentName: groups.get(taEmailTime) ){
                System.out.printf( "%-25s %-25s\n", studentName, studentFile.getStudentEmail(studentName));
            }
            System.out.println();
        }
    }


    public static void main( String[] args ){

        //Arg[0] is objective function

        //Arg[2] is student file
        //Arg[3] is solution from solver
        new Validate(args[0],args[1], args[2]).printRosters();

    }


}
