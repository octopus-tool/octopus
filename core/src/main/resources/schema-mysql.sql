-- 用户表
CREATE TABLE `user`
(
    `id`       bigint       NOT NULL AUTO_INCREMENT comment '主键',
    `username` varchar(64)  not null comment '用户名',
    `password` varchar(512) not null comment '密码',
    `enabled`  tinyint(4)   not null comment '是否启用',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8 comment ='用户表';

-- 数据源表
CREATE TABLE `datasource`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT comment '主键',
    `name`        varchar(64) not null comment '名称',
    `type`        tinyint(4) comment '类型',
    `description` varchar(255) comment '描述',
    `plugin_name` varchar(64) not null comment '插件名称',
    `content`     longtext    not null comment '数据源内容',
    `create_time` datetime    DEFAULT NULL,
    `create_by`   varchar(32) DEFAULT NULL,
    `update_time` datetime    DEFAULT NULL,
    `update_by`   varchar(32) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8 comment ='数据源表';

-- 数据源接口表
CREATE TABLE `datasource_interface`
(
    `id`            bigint      NOT NULL AUTO_INCREMENT comment '主键',
    `name`          varchar(64) not null comment '名称',
    `datasource_id` int         not null comment '数据源ID',
    `description`   varchar(255) comment '描述',
    `content`       longtext    not null comment '接口内容',
    `create_time`   datetime    DEFAULT NULL,
    `create_by`     varchar(32) DEFAULT NULL,
    `update_time`   datetime    DEFAULT NULL,
    `update_by`     varchar(32) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8 comment ='数据源接口表';
