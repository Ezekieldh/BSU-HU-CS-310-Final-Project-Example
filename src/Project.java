import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This application will keep track of things like what classes are offered by
 * the school, and which students are registered for those classes and provide
 * basic reporting. This application interacts with a database to store and
 * retrieve data.
 */
public class SchoolManagementSystem {

    public static void getAllClassesByInstructor(String first_name, String last_name) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = DriverManager.getConnection("jdbc:mysql://localhost:55958/cs_hu_310_final_project", "msandbox", "IkY@41WPp04");
        	sqlStatement = connection.createStatement();
        	
        	String sql = String.format("SELECT * FROM instructors as i"
											+ "\nJOIN class_sections as c_s ON c_s.instructor_id = i.instructor_id"
											+ "\nJOIN academic_titles as a_t ON a_t.academic_title_id = i.academic_title_id"
											+ "\nJOIN classes as c ON c.class_id = c_s.class_id"
											+ "\nJOIN terms as t ON t. term_id = c_s.term_id"
											+ "\nWHERE first_name LIKE '%s' AND last_name LIKE '%s';", first_name, last_name);
        	
        	ResultSet rs = sqlStatement.executeQuery(sql);
        	System.out.println("First Name | Last Name | Title | Code | Name | Term");
        	while(rs.next()) {
        		String instructor_first_name = rs.getString("first_name");
        		String instructor_last_name = rs.getString("last_name");
        		String title = rs.getString("title");
        		String code = rs.getString("code");
        		String name = rs.getString("c.name");
        		String term = rs.getString("t.name");
        		
        		System.out.println("-".repeat(80));
        		System.out.println(instructor_first_name + " | " + instructor_last_name + " | " + title +
        				" | " + code + " | " + name + " | " + term);
        	}
        	
        	rs.close();
            
        } catch (SQLException sqlException) {
            System.out.println("Failed to get class sections");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

    }

    public static void submitGrade(String studentId, String classSectionID, String grade) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = DriverManager.getConnection("jdbc:mysql://localhost:55958/cs_hu_310_final_project", "msandbox", "IkY@41WPp04");
        	sqlStatement = connection.createStatement();
        	
        	String sql = String.format("UPDATE class_registrations"
        							+ "\nSET grade_id = (SELECT grade_id FROM grades WHERE letter_grade LIKE '%s')"
        							+ "\nWHERE student_id = '%s' AND class_section_id = '%s';", grade, studentId, classSectionID);
        	
        	sqlStatement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
           	
        	System.out.println("-".repeat(80));
        	System.out.println("Grade has been submitted!");
        	
        } catch (SQLException sqlException) {
            System.out.println("Failed to submit grade");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void registerStudent(String studentId, String classSectionID) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = DriverManager.getConnection("jdbc:mysql://localhost:55958/cs_hu_310_final_project", "msandbox", "IkY@41WPp04");
        	sqlStatement = connection.createStatement();
        	
        	String sql = String.format("INSERT INTO class_registrations (student_id, class_section_id) "
        								+ "VALUES ('%s', '%s');", studentId, classSectionID);
        	
        	sqlStatement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        	
        	ResultSet rs = sqlStatement.getGeneratedKeys();
        	rs.next();
        	
        	int class_registration_id = rs.getInt(1);
        	
        	System.out.println("Class Registration ID | Student ID | Class Section ID");
        	System.out.println("-".repeat(80));
        	System.out.println(class_registration_id + " | " + studentId + " | " + classSectionID);
        	
        } catch (SQLException sqlException) {
            System.out.println("Failed to register student");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void deleteStudent(String studentId) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = DriverManager.getConnection("jdbc:mysql://localhost:55958/cs_hu_310_final_project", "msandbox", "IkY@41WPp04");
        	sqlStatement = connection.createStatement();
        	
        	String sql = String.format("DELETE FROM students WHERE student_id = '%s'", studentId);
        	
        	sqlStatement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
       	
        	System.out.println("-".repeat(80));
        	System.out.println("Student with id: " + studentId + " was deleted");
        	
        } catch (SQLException sqlException) {
            System.out.println("Failed to delete student");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }


    public static void createNewStudent(String firstName, String lastName, String birthdate) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = DriverManager.getConnection("jdbc:mysql://localhost:55958/cs_hu_310_final_project", "msandbox", "IkY@41WPp04");
        	sqlStatement = connection.createStatement();
        	
        	String sql = String.format("INSERT INTO students (first_name, last_name, birthdate) "
        								+ "VALUES ('%s', '%s', '%s');", firstName, lastName, birthdate);
        	
        	sqlStatement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
        	
        	ResultSet rs = sqlStatement.getGeneratedKeys();
        	rs.next();
        	
        	int student_id = rs.getInt(1);
        	
        	System.out.println("Student ID | First Name | Last Name | Birthdate");
        	System.out.println("-".repeat(80));
        	System.out.println(student_id + " | " + firstName + " | " + lastName + " | " + birthdate);
        	
        } catch (SQLException sqlException) {
            System.out.println("Failed to create student");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

    }

    public static void listAllClassRegistrations() {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = DriverManager.getConnection("jdbc:mysql://localhost:55958/cs_hu_310_final_project", "msandbox", "IkY@41WPp04");
        	sqlStatement = connection.createStatement();
        	
        	ResultSet rs = sqlStatement.executeQuery("SELECT * FROM students as s"
        											+ "\nLEFT JOIN class_registrations as c_r ON c_r.student_id = s.student_id"
        											+ "\nLEFT JOIN grades as g ON g.grade_id = c_r.grade_id"
        											+ "\nLEFT JOIN class_sections as c_s ON c_s.class_section_id = c_r.class_section_id"
        											+ "\nLEFT JOIN classes as c ON c.class_id = c_s.class_id"
        											+ "\nLEFT JOIN terms as t ON t.term_id = c_s.term_id"
        											+ "\nORDER BY first_name");
        	System.out.println("Student ID | Class Section ID | First Name | Last Name | Code | Name | Term | Letter Grade");
        	while(rs.next()) {
        		int student_id = rs.getInt("student_id");
        		String class_section_id = rs.getString("class_section_id");
        		String first_name = rs.getString("first_name");
        		String last_name = rs.getString("last_name");
        		String code = rs.getString("code");
        		String class_name = rs.getString("c.name");
        		String term_name = rs.getString("t.name");
        		String letter_grade = rs.getString("letter_grade");
        		
        		System.out.println("-".repeat(80));
        		System.out.println(student_id + " | " + class_section_id + " | " + first_name + 
        				" | " + last_name + " | " + code + " | " + class_name + " | " + term_name +
        				" | " + letter_grade);
        	}
        	
        	rs.close();
        } catch (SQLException sqlException) {
            System.out.println("Failed to get class sections");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void listAllClassSections() {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = DriverManager.getConnection("jdbc:mysql://localhost:55958/cs_hu_310_final_project", "msandbox", "IkY@41WPp04");
        	sqlStatement = connection.createStatement();
        	
        	ResultSet rs = sqlStatement.executeQuery("SELECT * FROM class_sections"
        											+ "\nJOIN classes ON classes.class_id = class_sections.class_id"
        											+ "\nJOIN terms ON terms.term_id = class_sections.class_id");
        	System.out.println("Class Section ID | Code | Name | Term");
        	while(rs.next()) {
        		int class_section_id = rs.getInt("class_section_id");
        		String code = rs.getString("code");
        		String name = rs.getString("classes.name");
        		String term = rs.getString("terms.name");
        		
        		System.out.println("-".repeat(80));
        		System.out.println(class_section_id + " | " + code + " | " + name +
        				" | " + term);
        	}
        	
        	rs.close();
        } catch (SQLException sqlException) {
            System.out.println("Failed to get class sections");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void listAllClasses() {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = DriverManager.getConnection("jdbc:mysql://localhost:55958/cs_hu_310_final_project", "msandbox", "IkY@41WPp04");
        	sqlStatement = connection.createStatement();
        	
        	ResultSet rs = sqlStatement.executeQuery("SELECT * FROM classes");
        	System.out.println("Class ID | Code | Name | Description");
        	while(rs.next()) {
        		int class_id = rs.getInt("class_id");
        		String code = rs.getString("code");
        		String name = rs.getString("name");
        		String description = rs.getString("description");
        		
        		System.out.println("-".repeat(80));
        		System.out.println(class_id + " | " + code + " | " + name +
        				" | " + description);
        	}
        } catch (SQLException sqlException) {
            System.out.println("Failed to get students");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }


    public static void listAllStudents() {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = DriverManager.getConnection("jdbc:mysql://localhost:55958/cs_hu_310_final_project", "msandbox", "IkY@41WPp04");
        	sqlStatement = connection.createStatement();
        	
        	ResultSet rs = sqlStatement.executeQuery("SELECT * FROM students;");
        	System.out.println("Student ID | First Name | Last Name | Birthdate");
        	while(rs.next()) {
        		int student_id = rs.getInt("student_id");
        		String first_name = rs.getString("first_name");
        		String last_name = rs.getString("last_name");
        		java.sql.Date birthdate = rs.getDate("birthdate");
        		
        		System.out.println("-".repeat(80));
        		System.out.println(student_id + " | " + first_name + " | " + last_name +
        				" | " + birthdate);
        	}
        	
        	rs.close();
        } catch (SQLException sqlException) {
            System.out.println("Failed to get students");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    /***
     * Splits a string up by spaces. Spaces are ignored when wrapped in quotes.
     *
     * @param command - School Management System cli command
     * @return splits a string by spaces.
     */
    public static List<String> parseArguments(String command) {
        List<String> commandArguments = new ArrayList<String>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(command);
        while (m.find()) commandArguments.add(m.group(1).replace("\"", ""));
        return commandArguments;
    }

    public static void main(String[] args) {
        System.out.println("Welcome to the School Management System");
        System.out.println("-".repeat(80));

        Scanner scan = new Scanner(System.in);
        String command = "";

        do {
            System.out.print("Command: ");
            command = scan.nextLine();
            ;
            List<String> commandArguments = parseArguments(command);
            command = commandArguments.get(0);
            commandArguments.remove(0);

            if (command.equals("help")) {
                System.out.println("-".repeat(38) + "Help" + "-".repeat(38));
                System.out.println("test connection \n\tTests the database connection");

                System.out.println("list students \n\tlists all the students");
                System.out.println("list classes \n\tlists all the classes");
                System.out.println("list class_sections \n\tlists all the class_sections");
                System.out.println("list class_registrations \n\tlists all the class_registrations");
                System.out.println("list instructor <first_name> <last_name>\n\tlists all the classes taught by that instructor");


                System.out.println("delete student <studentId> \n\tdeletes the student");
                System.out.println("create student <first_name> <last_name> <birthdate> \n\tcreates a student");
                System.out.println("register student <student_id> <class_section_id>\n\tregisters the student to the class section");

                System.out.println("submit grade <studentId> <class_section_id> <letter_grade> \n\tcreates a student");
                System.out.println("help \n\tlists help information");
                System.out.println("quit \n\tExits the program");
            } else if (command.equals("test") && commandArguments.get(0).equals("connection")) {
                Database.testConnection();
            } else if (command.equals("list")) {
                if (commandArguments.get(0).equals("students")) listAllStudents();
                if (commandArguments.get(0).equals("classes")) listAllClasses();
                if (commandArguments.get(0).equals("class_sections")) listAllClassSections();
                if (commandArguments.get(0).equals("class_registrations")) listAllClassRegistrations();

                if (commandArguments.get(0).equals("instructor")) {
                    getAllClassesByInstructor(commandArguments.get(1), commandArguments.get(2));
                }
            } else if (command.equals("create")) {
                if (commandArguments.get(0).equals("student")) {
                    createNewStudent(commandArguments.get(1), commandArguments.get(2), commandArguments.get(3));
                }
            } else if (command.equals("register")) {
                if (commandArguments.get(0).equals("student")) {
                    registerStudent(commandArguments.get(1), commandArguments.get(2));
                }
            } else if (command.equals("submit")) {
                if (commandArguments.get(0).equals("grade")) {
                    submitGrade(commandArguments.get(1), commandArguments.get(2), commandArguments.get(3));
                }
            } else if (command.equals("delete")) {
                if (commandArguments.get(0).equals("student")) {
                    deleteStudent(commandArguments.get(1));
                }
            } else if (!(command.equals("quit") || command.equals("exit"))) {
                System.out.println(command);
                System.out.println("Command not found. Enter 'help' for list of commands");
            }
            System.out.println("-".repeat(80));
        } while (!(command.equals("quit") || command.equals("exit")));
        System.out.println("Bye!");
    }
}
