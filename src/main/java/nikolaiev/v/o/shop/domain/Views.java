package nikolaiev.v.o.shop.domain;

//класс использеться в JsonView
public final class Views {
    public interface IdName {}
    public interface IdNameSrc extends IdName {}
    public interface Type  extends IdName {}
    public interface OnlyChild extends Type {}
    public interface FullLinkedDirectory extends OnlyChild {}
    public interface FullMessage extends FullLinkedDirectory {}

}
