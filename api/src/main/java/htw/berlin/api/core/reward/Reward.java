package htw.berlin.api.core.reward;

import java.time.ZonedDateTime;

public class Reward {
    private int rewardId;
    private String studentId;
    private RewardType rewardType;
    private boolean enableReward;
    private boolean isReward;
    private String serviceAddress;

    public Reward() {
        rewardId = 0;
        studentId = null;
        rewardType = null;
        enableReward = false;
        isReward = false;
        serviceAddress = null;
    }

    public Reward(int rewardId, String studentId, RewardType rewardType, boolean enableReward, boolean isReward,String serviceAddress) {
        this.rewardId = rewardId;
        this.studentId = studentId;
        this.rewardType = rewardType;
        this.isReward = isReward;
        this.enableReward = enableReward;
        this.serviceAddress = serviceAddress;
    }

    public int getRewardId() {
        return rewardId;
    }

    public void setRewardId(int rewardId) {
        this.rewardId = rewardId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public RewardType getRewardType() {
        return rewardType;
    }

    public void setRewardType(RewardType rewardType) {
        this.rewardType = rewardType;
    }

    public boolean isEnableReward() {
        return enableReward;
    }

    public void setEnableReward(boolean enableReward) {
        this.enableReward = enableReward;
    }

    public boolean isReward() {
        return isReward;
    }

    public void setReward(boolean reward) {
        isReward = reward;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }
}
