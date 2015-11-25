--
-- Structure for table extend_follow
--
DROP TABLE IF EXISTS extend_follow;
CREATE TABLE extend_follow (
	id_follow INT DEFAULT 0 NOT NULL,
	id_resource VARCHAR(100) DEFAULT '' NOT NULL,
	resource_type VARCHAR(255) DEFAULT '' NOT NULL,
	follow_count INT default 0 NOT NULL,
	PRIMARY KEY (id_follow)
);

--
-- Structure for table extend_follow_vote_history
--
DROP TABLE IF EXISTS extend_follow_history;
CREATE TABLE extend_follow_history (
	id_follow_history INT DEFAULT 0 NOT NULL,
	id_extender_history INT DEFAULT 0 NOT NULL,
	follow_value INT DEFAULT 0 NOT NULL,
	PRIMARY KEY (id_follow_history)
);