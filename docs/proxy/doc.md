```
user nginx;
worker_processes auto;
worker_rlimit_nofile 51200;

events {
    worker_connections 1024;
}

http {
    resolver 8.8.8.8  ipv6=off;
    proxy_ssl_server_name on;

    access_log off;
    server_names_hash_bucket_size 512;
    client_header_buffer_size 64k;
    large_client_header_buffers 4 64k;
    client_max_body_size 50M;

    proxy_connect_timeout       240s;
    proxy_read_timeout          240s;
    proxy_buffer_size 128k;
    proxy_buffers 4 256k;

    server {
        listen 80;
        server_name oiawkhywuxhs.cloud.sealos.io;

        location ~ /openai/(.*) {
            proxy_pass https://api.openai.com/$1$is_args$args;
            proxy_set_header Host api.openai.com;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            # 如果响应是流式的
            proxy_set_header Connection '';
            proxy_http_version 1.1;
            chunked_transfer_encoding off;
            proxy_buffering off;
            proxy_cache off;
            # 如果响应是一般的
            proxy_buffer_size 128k;
            proxy_buffers 4 256k;
            proxy_busy_buffers_size 256k;
        }

        location ~ /google/(.*) {
            proxy_pass https://generativelanguage.googleapis.com/$1$is_args$args;
            proxy_set_header Host generativelanguage.googleapis.com;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            # 如果响应是流式的
            proxy_set_header Connection '';
            proxy_http_version 1.1;
            chunked_transfer_encoding off;
            proxy_buffering off;
            proxy_cache off;
            # 如果响应是一般的
            proxy_buffer_size 128k;
            proxy_buffers 4 256k;
            proxy_busy_buffers_size 256k;
        }

        location ~ /anthropic/(.*) {
            proxy_pass https://api.anthropic.com/$1$is_args$args;
            proxy_set_header Host api.anthropic.com;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            # 如果响应是流式的
            proxy_set_header Connection '';
            proxy_http_version 1.1;
            chunked_transfer_encoding off;
            proxy_buffering off;
            proxy_cache off;
            # 如果响应是一般的
            proxy_buffer_size 128k;
            proxy_buffers 4 256k;
            proxy_busy_buffers_size 256k;
        }
        location ~ /xai/(.*) {
            proxy_pass https://api.x.ai/$1$is_args$args;
            proxy_set_header Host api.x.ai;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            # 如果响应是流式的
            proxy_set_header Connection '';
            proxy_http_version 1.1;
            chunked_transfer_encoding off;
            proxy_buffering off;
            proxy_cache off;
            # 如果响应是一般的
            proxy_buffer_size 128k;
            proxy_buffers 4 256k;
            proxy_busy_buffers_size 256k;
        }
        location ~ /openrouter/(.*) {
            proxy_pass https://openrouter.ai/api/$1$is_args$args;
            proxy_set_header Host openrouter.ai;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            # 如果响应是流式的
            proxy_set_header Connection '';
            proxy_http_version 1.1;
            chunked_transfer_encoding off;
            proxy_buffering off;
            proxy_cache off;
            # 如果响应是一般的
            proxy_buffer_size 128k;
            proxy_buffers 4 256k;
            proxy_busy_buffers_size 256k;
        }
        location ~ /poe/(.*) {
            proxy_pass https://api.poe.com/$1$is_args$args;
            proxy_set_header Host api.poe.com;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            # 如果响应是流式的
            proxy_set_header Connection '';
            proxy_http_version 1.1;
            chunked_transfer_encoding off;
            proxy_buffering off;
            proxy_cache off;
            # 如果响应是一般的
            proxy_buffer_size 128k;
            proxy_buffers 4 256k;
            proxy_busy_buffers_size 256k;
        }
        location /proxy/ {
            # 从参数获取目标地址
            set $target_host "";
            set $target_url "";
            
            # 安全校验：只允许特定域名或验证token
            if ($arg_token != "ShuYou_20250711") {
                return 403 "Invalid token";
            }
            
            # 获取目标主机和路径
            if ($arg_host) {
                set $target_host $arg_host;
            }
            
            if ($arg_url) {
                set $target_url $arg_url;
            }
            
            proxy_pass https://$target_host$target_url;
            proxy_set_header Host $target_host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            
            # 重要：清除原始请求头，防止信息泄露
            proxy_set_header Origin "";
            proxy_set_header Referer "";
            
            # 超时设置
            proxy_connect_timeout 30s;
            proxy_send_timeout 30s;
            proxy_read_timeout 60s;
        }
    }
}
```
