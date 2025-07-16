# Fight-Booking

本项目参考了[*spring-ai-examples*][1]，另外要特别感谢 **徐庶** 老师的分享。

## 前提
1. 申请[*阿里百炼key*][2]  
2. 申请[*Google Gemini key*][3]

## 项目结构
```plaintext
> fight-booking     // 4. 运行Spring Boot应用
 > flight-booking-app // 3. 前端, 通过 `npm run dev` 启动
 > mcp-server-sse   // 2. build, 作为MCP服务器，需预先启动
 > mcp-server-stdio // 1. build, 作为本地MCP服务器，无需预先启动
```

*** 
---  
---  


[1]: https://github.com/spring-projects/spring-ai-examples
[2]: https://bailian.console.aliyun.com/?tab=model&spm=0.0.0.i1#/api-key
[3]: https://aistudio.google.com/apikey

