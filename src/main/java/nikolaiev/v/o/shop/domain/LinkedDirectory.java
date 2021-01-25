package nikolaiev.v.o.shop.domain;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
public class LinkedDirectory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(Views.IdName.class)
    private Long id;

    //название директории
    @JsonView(Views.IdName.class)
    private String name;

    //тип директории, от него зависит к какой групе относиться дериктория
    @JsonView(Views.Type.class)
    private String directoryType;

    //подкатегории
    @OneToMany
    @JsonView(Views.OnlyChild.class)
    private Set<LinkedDirectory> children;

    //список связаных с этой директорией директории,
    //используеться в блоке фильтра результатов,
    //в этот список попадают директории которые имеют другие товары
    @ManyToMany
    private Set<LinkedDirectory> relatedDirectories;

    //хранит список индентификаторов id сязаных директорий (relatedDirectories)
    @ElementCollection
    @CollectionTable(
            name = "directories_associated_with_this_directory",
            joinColumns = @JoinColumn(name = "directory_id")
    )
    @Column(name = "related_directory")
    //передаеться в файле ответа JSON, вместо relatedDirectories, для того чтобы не столкнуться с рекурсией
    @JsonView(Views.FullLinkedDirectory.class)
    private Set<Long> relatedDirectoryIds;

    //надкатегория, ссылка на директории высшую на один уровень
    @ManyToOne
    private LinkedDirectory father;

    //продукты которые привязаные к даной директории
    @ManyToMany
    //@JsonView(Views.FullMessage.class)
    private Set<Product> products;

    //хранит количество элементов списка products
    @JsonView(Views.FullLinkedDirectory.class)
    private Long productsCount;


    public LinkedDirectory () {
    }


    /**
     * Создать директориию у которой тип и имя одинаковые
     * @param directoryType тип категории
     */
    public LinkedDirectory (Enum<DirectoryType> directoryType) {
        this.id = 0l;
        this.name = directoryType.toString ();
        this.directoryType = directoryType.toString ();
        this.children = new HashSet<> ();
        this.relatedDirectories = new HashSet<> ();
        this.relatedDirectoryIds = new HashSet<> ();
        this.father = null;
        this.products = new HashSet<> ();
        this.productsCount = 0l;
    }

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public Set<Product> getProducts () {
        return products;
    }

    public void setProducts (Set<Product> products) {
        this.products = products;
    }

    public void addProduct (Product productFromDb) {
        this.products.add (productFromDb);
    }

    public void deleteProduct (Product productFromDb) {
        this.products.remove (productFromDb);
    }

    public Long getProductsCount () {
        return productsCount;
    }

    public void setProductsCount (Long productsCount) {
        this.productsCount = productsCount;
    }

    public Set<LinkedDirectory> getChildren () {
        return children;
    }

    public void setChildren (Set<LinkedDirectory> children) {
        this.children = children;
    }

    public void addChild(LinkedDirectory child){
        this.children.add (child);
    }

    public void deleteChild(LinkedDirectory child){
        this.children.remove (child);
    }

    public Set<LinkedDirectory> getRelatedDirectories () {
        return relatedDirectories;
    }

    public void setRelatedDirectories (Set<LinkedDirectory> relatedDirectories) {
        this.relatedDirectories = relatedDirectories;
    }


    /**
     * Добавляет связаную директорию
     *
     * @param relatedDirectory связаная директория
     */
    public void addRelatedDirectory(LinkedDirectory relatedDirectory){
        this.relatedDirectories.add (relatedDirectory);
    }

    public void deleteRelatedDirectory(LinkedDirectory relatedDirectory){
        this.relatedDirectories.remove (relatedDirectory);
    }

    public Set<Long> getRelatedDirectoryIds () {
        return relatedDirectoryIds;
    }

    public void setRelatedDirectoryIds (Set<Long> relatedDirectoryIds) {
        this.relatedDirectoryIds = relatedDirectoryIds;
    }

    /**
     * Добавляет id связаной директории
     *
     * @param relatedDirectoryId id связаной директории
     */
    public void addRelatedDirectoryId(Long relatedDirectoryId){
        this.relatedDirectoryIds.add (relatedDirectoryId);
    }

    public void deleteRelatedDirectoryId(Long relatedDirectoryid){
        this.relatedDirectoryIds.remove (relatedDirectoryid);
    }

    public String getDirectoryType () {
        return directoryType;
    }

    public void setDirectoryType (String directoryType) {
        this.directoryType = directoryType;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public LinkedDirectory getFather () {
        return father;
    }

    public void setFather (LinkedDirectory father) {
        this.father = father;
    }
}
