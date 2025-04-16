/**
Create Java applications using Spring and Hibernate for dependency injection, CRUD operations, and transaction management. 
Easy Level: Create a simple Spring application that demonstrates Dependency Injection (DI) using Java-based configuration instead of XML.
Define a Student class that depends on a Course class. Use Spring’s @Configuration and @Bean annotations to inject dependencies. Requirements: 
Define a Course class with attributes courseName and duration. Define a Student class with attributes name and a reference to Course. Use Java-based configuration 
(@Configuration and @Bean) to configure the beans. Load the Spring context in the main method and print student details.
*/

//Course Class
public class Course {
    private String courseName;
    private int duration;

    public Course(String courseName, int duration) {
        this.courseName = courseName;
        this.duration = duration;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "Course Name: " + courseName + ", Duration: " + duration + " months";
    }
}
// Student Class
public class Student {
    private String name;
    private Course course;

    public Student(String name, Course course) {
        this.name = name;
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public Course getCourse() {
        return course;
    }

    public void displayStudentDetails() {
        System.out.println("Student Name: " + name);
        System.out.println(course);
    }
}
//beans
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public Course course() {
        return new Course("Spring Boot", 3);
    }

    @Bean
    public Student student() {
        return new Student("Sagar", course());
    }
}
// java spring
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainApp {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        Student student = context.getBean(Student.class);
        student.displayStudentDetails();
    }
}
/**
Medium Level: Develop a Hibernate-based application to perform CRUD (Create, Read, Update, Delete) operations on a Student entity using Hibernate ORM with MySQL. 
Requirements: Configure Hibernate using hibernate.cfg.xml. Create an Entity class (Student.java) with attributes: id, name, and age. 
Implement Hibernate SessionFactory to perform CRUD operations. Test the CRUD functionality with sample data. 
*/
//Hibernate xml
<!DOCTYPE hibernate-configuration SYSTEM "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <property name="hibernate.connection.driver_class">com.mysql.cj.jdbc.Driver</property>
        <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/school</property>
        <property name="hibernate.connection.username">root</property>
        <property name="hibernate.connection.password">password</property>
        <property name="hibernate.dialect">org.hibernate.dialect.MySQLDialect</property>
        <property name="hibernate.hbm2ddl.auto">update</property>
        <property name="show_sql">true</property>
        <mapping class="com.example.Student"/>
    </session-factory>
</hibernate-configuration>

  //student entity
  import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String name;
    private int age;

    public Student() {}

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Getters and Setters
}

//hibernate session factory
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
//CRUD operation
import org.hibernate.Session;
import org.hibernate.Transaction;

public class StudentDAO {
    public void saveStudent(Student student) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.save(student);
        tx.commit();
        session.close();
    }

    public Student getStudentById(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Student student = session.get(Student.class, id);
        session.close();
        return student;
    }

    public void updateStudent(int id, String newName, int newAge) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Student student = session.get(Student.class, id);
        student.setName(newName);
        student.setAge(newAge);
        session.update(student);
        tx.commit();
        session.close();
    }

    public void deleteStudent(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        Student student = session.get(Student.class, id);
        session.delete(student);
        tx.commit();
        session.close();
    }
}
// test CRUD
public class MainApp {
    public static void main(String[] args) {
        StudentDAO dao = new StudentDAO();
        
        // Create Student
        dao.saveStudent(new Student("Sagar", 22));

        // Read Student
        System.out.println(dao.getStudentById(1));

        // Update Student
        dao.updateStudent(1, "Sagar Sharma", 23);

        // Delete Student
        dao.deleteStudent(1);
    }
}
/**
Hard Level: 
Develop a Spring-based application integrated with Hibernate to manage transactions. Create a banking system where users can transfer money between accounts, ensuring transaction consistency. 
Requirements: Use Spring configuration with Hibernate ORM. Implement two entity classes (Account.java and Transaction.java). Use Hibernate Transaction Management to ensure atomic operations. 
If a transaction fails, rollback should occur. Demonstrate successful and failed transactions.
*/
//Account entity
import jakarta.persistence.*;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String holderName;
    private double balance;

    // Constructors, Getters, Setters
}
// transacation entity
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int senderId;
    private int receiverId;
    private double amount;

    // Constructors, Getters, Setters
}
// transaction management
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankingService {
    @Autowired
    private AccountDAO accountDAO;

    @Transactional
    public void transferMoney(int senderId, int receiverId, double amount) throws Exception {
        Account sender = accountDAO.getAccountById(senderId);
        Account receiver = accountDAO.getAccountById(receiverId);

        if (sender.getBalance() >= amount) {
            sender.setBalance(sender.getBalance() - amount);
            receiver.setBalance(receiver.getBalance() + amount);
            accountDAO.updateAccount(sender);
            accountDAO.updateAccount(receiver);
        } else {
            throw new Exception("Insufficient balance! Transaction failed.");
        }
    }
}
//xml
<tx:annotation-driven/>
<bean id="transactionManager" class="org.springframework.orm.hibernate5.HibernateTransactionManager">
    <property name="sessionFactory" ref="sessionFactory"/>
</bean>
// test transaction
  public class BankingApp {
    public static void main(String[] args) {
        try {
            bankingService.transferMoney(1, 2, 100);
            System.out.println("Transaction Successful!");
        } catch (Exception e) {
            System.out.println("Transaction Failed: " + e.getMessage());
        }
    }
}
