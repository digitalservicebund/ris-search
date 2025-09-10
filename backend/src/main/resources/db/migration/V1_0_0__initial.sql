CREATE TABLE IF NOT EXISTS ecli_crawler_publications (
    id varchar(20),
    ecli varchar(50),
    court_type varchar(50),
    decision_date varchar(10),
    document_type varchar(50),
    is_published boolean,

    PRIMARY KEY(id)
)
