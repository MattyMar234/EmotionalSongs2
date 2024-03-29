package enumclass;

/**
 * Enumerazione che rappresenta le parole chiave SQL.
 * Ogni valore enum corrisponde a una parola chiave specifica utilizzata nei comandi SQL.
 */
public enum SQLKeyword {
    NUMERIC("NUMERIC"),
    DECIMAL("DECIMAL"),
    SMALLINT("SMALLINT"),
    INTEGER("INTEGER"),
    TIMESTAMP("TIMESTAMP"),
    VARCHAR("VARCHAR"),
    NVARCHAR("NVARCHAR"),
    VARBINARY("VARBINARY"),
    DOUBLE("DOUBLE"),
    BOOLEAN("BOOLEAN"),
    FLOAT("FLOAT"),
    CHAR("CHAR"),
    BLOB("BLOB"),
    CLOB("CLOB"),
    DATE("DATE"),
    TIME("TIME"),
    TEXT("TEXT"),
    BIT("BIT"),
    JOIN("JOIN"),
    LEFT("LEFT"),
    RIGHT("RIGHT"),
    OUTER("OUTER"),
    INNER("INNER"),
    EXISTS("EXISTS"),
    HAVING("HAVING"),
    SELECT("SELECT"),
    WHERE("WHERE"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    INSERT("INSERT"),
    ALTER("ALTER"),
    CREATE("CREATE"),
    DROP("DROP"),
    INDEX("INDEX"),
    CONSTRAINT("CONSTRAINT"),
    PRIMARY("PRIMARY"),
    FOREIGN("FOREIGN"),
    REFERENCES("REFERENCES"),
    TABLE("TABLE"),
    VIEW("VIEW"),
    PROCEDURE("PROCEDURE"),
    FUNCTION("FUNCTION"),
    DECLARE("DECLARE"),
    OFFSET("OFFSET"),
    SET("SET"),
    BEGIN("BEGIN"),
    COMMIT("COMMIT"),
    ROLLBACK("ROLLBACK"),
    GRANT("GRANT"),
    REVOKE("REVOKE"),
    USER("USER"),
    DATABASE("DATABASE"),
    CURSOR("CURSOR"),
    SHOW("SHOW"),
    MAX("MAX"),
    MIN("MIN"),
    AVG("AVG"),
    COUNT("COUNT"),
    SUM("SUM"),
    DISTINCT("DISTINCT"),
    ORDER("ORDER"),
    BIGINT("BIGINT"),
    BY("BY"),
    GROUP("GROUP"),
    ASC("ASC"),
    DESC("DESC"),
    FOR("FOR"),
    IF("IF"),
    KEY("KEY"),
    WHEN("WHEN"),
    THEN("THEN"),
    ELSE("ELSE"),
    END("END"),
    ALL("ALL"),
    AS("AS"),
    ON("ON"),
    AND("AND"),
    OR("OR"),
    NOT("NOT"),
    IN("IN"),
    BETWEEN("BETWEEN"),
    LIKE("LIKE"),
    IS("IS"),
    LIMIT("LIMIT"),
    FROM("FROM"),
    CASCADE("CASCADE"),
    NULL("NULL");

    private final String keyword;

    SQLKeyword(String keyword) {
        this.keyword = keyword;
    }


    
/**
 * Restituisce la parola chiave associata a un oggetto di questa classe.
 *
 * Questo metodo restituisce la parola chiave memorizzata nell'oggetto corrente.
 *
 * @return La parola chiave associata all'oggetto.
 */
    public String getKeyword() {
        return keyword;
    }
}


