
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
  dtype character varying(31) NOT NULL,
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

CREATE TABLE annotation
(
  dtype character varying(31) NOT NULL,
  id integer NOT NULL,
  predicatenamespace character varying(255),
  datatype character varying(255),
  property character varying(255),
  "value" character varying(255),
  href character varying(255),
  rel character varying(255),
  CONSTRAINT annotation_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE annotation OWNER TO phyloviewer;

CREATE TABLE node_annotation
(
  node_node_id integer NOT NULL,
  annotations_id integer NOT NULL,
  CONSTRAINT node_annotation_pkey PRIMARY KEY (node_node_id, annotations_id),
  CONSTRAINT fk1ab6f9ac1f6282ba FOREIGN KEY (node_node_id)
      REFERENCES node (node_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk1ab6f9acbfdd08a0 FOREIGN KEY (annotations_id)
      REFERENCES annotation (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT node_annotation_annotations_id_key UNIQUE (annotations_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE node_annotation OWNER TO phyloviewer;

CREATE TABLE nested_annotation
(
  annotation_id integer NOT NULL,
  nestedannotations_id integer NOT NULL,
  CONSTRAINT nested_annotation_pkey PRIMARY KEY (annotation_id, nestedannotations_id),
  CONSTRAINT fk5cde6f775e62ed57 FOREIGN KEY (nestedannotations_id)
      REFERENCES annotation (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk5cde6f77d8ebb7c8 FOREIGN KEY (annotation_id)
      REFERENCES annotation (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT nested_annotation_nestedannotations_id_key UNIQUE (nestedannotations_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE nested_annotation OWNER TO phyloviewer;

create table overview_images (
	tree_id bytea not null,
	layout_id varchar not null,
	image_width integer not null,
	image_height integer not null,
	image_path varchar not null
);

create index IndexParent on node(parent_node_id);
create index IndexLabel on node(lower(label::text));

COMMIT;
