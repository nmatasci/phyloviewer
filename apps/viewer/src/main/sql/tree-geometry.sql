
BEGIN;

create table node_layout (
	node_id integer not null,
	root_node_id integer not null,
	layout_id varchar,
	foreign key(node_id) references node(node_id) on delete cascade,
	foreign key(root_node_id) references node(node_id) on delete cascade
);

SELECT AddGeometryColumn('node_layout','point',-1,'POINT',2);
SELECT AddGeometryColumn('node_layout','bounding_box',-1,'POLYGON',2);

create index IndexLayout on node_layout(node_id, layout_id);
CREATE INDEX index_point ON node_layout USING GIST ( point );
CREATE INDEX index_bounding_box ON node_layout USING GIST ( bounding_box );

END;
