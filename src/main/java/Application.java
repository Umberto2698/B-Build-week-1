import com.github.javafaker.Faker;
import utils.JpaUtils;

import javax.persistence.EntityManager;
import java.util.Locale;
import java.util.Scanner;

public class Application {

    public static void main(String[] args) {
        EntityManager em = JpaUtils.getEmf().createEntityManager();

        Faker faker = new Faker(Locale.ITALY);

        Scanner input = new Scanner(System.in);
        try{

        }catch (Exception e){
            System.out.println(e);
        }finally {
            em.close();
            JpaUtils.close();
            input.close();
        }
    }
}
