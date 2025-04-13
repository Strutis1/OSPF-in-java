package constants;
//reduces the size of routers' lsdb, based on the area type by limiting the scope of LSA's allowed inside
public enum AreaType {
    STUB,
    TOTALLY_STUBBY,
    NSSA,       //not-so-stubby-area(what the fuck are the names?)
    TOTALLY_NSSA
}
