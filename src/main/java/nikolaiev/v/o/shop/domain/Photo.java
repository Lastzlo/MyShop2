package nikolaiev.v.o.shop.domain;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.FullMessage.class)
    private Long id;
    @JsonView(Views.FullMessage.class)
    private String name;
    //ссылка на файл
    @JsonView(Views.FullMessage.class)
    private String src;

    public Photo () {
    }

    public Photo (String name, String src) {
        this.name = name;
        this.src = src;
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getSrc () {
        return src;
    }

    public void setSrc (String src) {
        this.src = src;
    }


}
