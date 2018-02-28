package com.moinapp.wuliao.modules.sticker.model;

import com.moinapp.wuliao.bean.BaseHttpResponse;

import java.util.List;

/**
 * 请求服务器对象列表的结果
 */
public class GetServerInfoResult extends BaseHttpResponse {
    private List<ServerInfo> servers;

    public List<ServerInfo> getServers() {
        return servers;
    }

    public void setServers(List<ServerInfo> servers) {
        this.servers = servers;
    }
}
