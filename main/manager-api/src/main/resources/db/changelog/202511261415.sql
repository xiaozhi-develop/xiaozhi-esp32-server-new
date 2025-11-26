-- 更新服务器端口配置
UPDATE `sys_params` SET `param_value` = '9200' WHERE `param_code` = 'server.port';
UPDATE `sys_params` SET `param_value` = '9201' WHERE `param_code` = 'server.http_port';
