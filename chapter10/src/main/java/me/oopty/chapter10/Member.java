package me.oopty.chapter10;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(
            name="Member.findByUsername",
            query="select m from Member m where m.username = :username"),
        @NamedQuery(
                name="Member.count",
                query="select count(m) from Member m")
})
@SqlResultSetMapping(
        name = "memberWithOrderCount",
        entities = @EntityResult(entityClass = Member.class),
        columns = @ColumnResult(name = "ORDER_COUNT"))
@NamedNativeQuery(
        name="Member.memberWithOrderCount",
        query="select m.id, m.username, m.age, m.TEAM_ID, OM.ORDER_COUNT from member m left outer join (select o.MEMBER_ID as id, count(*) AS ORDER_COUNT from orders o group by o.MEMBER_ID) OM on m.id = OM.id",
        resultSetMapping = "memberWithOrderCount")
public class Member {
    @Id @GeneratedValue
    private Long id;
    private String username;
    private int age;

    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new LinkedList<>();

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public Member() {

    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
