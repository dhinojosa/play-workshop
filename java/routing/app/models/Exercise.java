package models;

import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Exercise {

    @Id
    private Long id;

    @Constraints.Required(message = "Name is required")
    private String name;

    @Constraints.Required(message = "Minutes are required")
    @Constraints.Min(value = 1, message = "Minutes must be more than 1")
    @Constraints.Max(value = 10 * 60, message = "Exercising for more than 10 hours a day is a bit much")
    private Integer minutes;

    public Exercise() {
    }

    public Exercise(String name, Integer minutes) {
        this.name = name;
        this.minutes = minutes;
    }


    public String getName() {
        return name;
    }

    public Integer getMinutes() {
        return minutes;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Exercise &&
                ((Exercise) object).name.equals(this.name);
    }

    @Override
    public int hashCode() { return name.hashCode() % 313; }

    @Override
    public String toString() {return String.format("Exercise{name=%s}", name);}

    public void setName(String name) {
        this.name = name;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }
}
