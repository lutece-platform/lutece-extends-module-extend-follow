--
-- Structure for table extend_follow_config
--
DROP TABLE IF EXISTS extend_follow_config;
CREATE TABLE extend_follow_config (
	id_extender INT DEFAULT 0 NOT NULL,
	authenticated_mode INT DEFAULT 0 NOT NULL,
	PRIMARY KEY (id_extender)
);

-- INIT TABLE extend_follow_config
INSERT INTO extend_follow_config ( id_extender, authenticated_mode ) 
	SELECT id_extender, 1 
    FROM extend_resource_extender 
    WHERE extender_type = 'follow' 
    AND id_extender NOT IN ( SELECT id_extender FROM extend_follow_config ) ;