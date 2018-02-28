package com.moinapp.wuliao.team.bean;

import com.moinapp.wuliao.bean.Entity;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("oschina")
public class TeamDiaryDetailBean extends Entity {

    @XStreamAlias("diary")
    private TeamDiary teamDiary;

    public TeamDiary getTeamDiary() {
        return teamDiary;
    }

    public void setTeamDiary(TeamDiary teamDiary) {
        this.teamDiary = teamDiary;
    }

}
