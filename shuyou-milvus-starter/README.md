# Milvus
官方地址： https://milvus.io/
文档：    https://milvus.io/docs
安装：    https://milvus.io/docs/zh/configure-docker.md?tab=component

https://github.com/milvus-io/milvus
https://gitee.com/dromara/MilvusPlus

图形工具：attu
https://github.com/zilliztech/attu?tab=readme-ov-file#running-attu-from-docker

```
wget https://github.com/milvus-io/milvus/releases/download/v2.6.0/milvus-standalone-docker-compose.yml -O docker-compose.yml
wget https://raw.githubusercontent.com/milvus-io/milvus/v2.6.0/configs/milvus.yaml
sudo docker compose up -d
sudo docker compose restart

docker run -p 3000:3000 -e MILVUS_URL=172.17.0.5:19530 zilliz/attu:v2.6

```