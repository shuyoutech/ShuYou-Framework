# 查看系统资源使用情况
docker stats

# 清理未使用的镜像和容器
docker system prune -a

# 查看所有容器状态
docker-compose ps

# 查看特定服务日志
docker-compose logs -f [service-name]

# 查看所有服务日志
docker-compose logs -f

# 启动服务
docker-compose up -d

# 停止服务
docker-compose down

# 重启服务
docker-compose restart

# 更新服务
docker-compose pull
docker-compose up -d

# 查看详细错误信息
docker-compose logs [service-name]

# 检查端口占用
netstat -tlnp | grep [port]

# 检查数据库容器状态
docker-compose ps mysql

# 查看数据库日志
docker-compose logs mysql