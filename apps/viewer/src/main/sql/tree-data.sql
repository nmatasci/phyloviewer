
BEGIN;

CREATE SEQUENCE hibernate_sequence
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 5
  CACHE 1;
ALTER TABLE hibernate_sequence OWNER TO phyloviewer;

CREATE TABLE node
(
  node_id integer NOT NULL,
  branchlength double precision,
  label character varying(255),
  altlabel character varying(255),
  branchlengthheight double precision NOT NULL,
  depth integer NOT NULL,
  height integer NOT NULL,
  leftindex integer NOT NULL,
  numchildren integer NOT NULL,
  numleaves integer NOT NULL,
  numnodes integer NOT NULL,
  rightindex integer NOT NULL,
  parent_node_id integer,
  rootnode_node_id integer,
  CONSTRAINT node_pkey PRIMARY KEY (node_id),
  CONSTRAINT fk33ae0243d48de0 FOREIGN KEY (rootnode_node_id)
      REFERENCES node (node_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk33ae02919982a6 FOREIGN KEY (parent_node_id)
      REFERENCES node (node_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE node OWNER TO phyloviewer;

CREATE TABLE tree
(
  tree_id integer NOT NULL,
  hash bytea,
  importcomplete boolean NOT NULL,
  "name" character varying(255),
  public boolean NOT NULL,
  rootnode_node_id integer,
  CONSTRAINT tree_pkey PRIMARY KEY (tree_id),
  CONSTRAINT fk36739e43d48de0 FOREIGN KEY (rootnode_node_id)
      REFERENCES node (node_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tree OWNER TO phyloviewer;

create table overview_images (
	tree_id integer not null,
	layout_id varchar not null,
	image_width integer not null,
	image_height integer not null,
	image_path varchar not null
);

create index IndexParent on node(parent_node_id);
create index IndexLabel on node(lower(label::text));

COMMIT;
