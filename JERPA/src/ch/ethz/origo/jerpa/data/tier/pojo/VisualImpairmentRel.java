package ch.ethz.origo.jerpa.data.tier.pojo;

/**
 * Created by IntelliJ IDEA.
 * User: Petr
 * Date: 14.12.11
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class VisualImpairmentRel {
    private int visualImpairmentId;

    public int getVisualImpairmentId() {
        return visualImpairmentId;
    }

    public void setVisualImpairmentId(int visualImpairmentId) {
        this.visualImpairmentId = visualImpairmentId;
    }

    private int personId;

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VisualImpairmentRel that = (VisualImpairmentRel) o;

        if (personId != that.personId) return false;
        if (visualImpairmentId != that.visualImpairmentId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = visualImpairmentId;
        result = 31 * result + personId;
        return result;
    }

    private Person person;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    private VisualImpairment visualImpairment;

    public VisualImpairment getVisualImpairment() {
        return visualImpairment;
    }

    public void setVisualImpairment(VisualImpairment visualImpairment) {
        this.visualImpairment = visualImpairment;
    }
}
