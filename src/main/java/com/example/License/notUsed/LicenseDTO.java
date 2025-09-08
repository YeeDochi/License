package com.example.License.notUsed;

public class LicenseDTO {

    private final Integer coreCount;
    private final Integer socketCount;
    private final String boardSerial;
    private final String macAddress;
    private final String expireDate;
    private final Integer type;

    private LicenseDTO(Builder builder) {
        this.coreCount = builder.coreCount;
        this.socketCount = builder.socketCount;
        this.boardSerial = builder.boardSerial;
        this.macAddress = builder.macAddress;
        this.expireDate = builder.expireDate;
        this.type = builder.type;
    }


    public static Builder builder() {
        return new Builder();
    }

    public Integer getCoreCount() { return coreCount; }
    public Integer getSocketCount() { return socketCount; }
    public String getBoardSerial() { return boardSerial; }
    public String getMacAddress() { return macAddress; }
    public String getExpireDate() { return expireDate; }
    public Integer getType() { return type; }

    @Override
    public String toString() {
        return "LicenseDTO{" +
                "coreCount=" + coreCount +
                ", socketCount=" + socketCount +
                ", boardSerial='" + boardSerial + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", expireDate='" + expireDate + '\'' +
                ", type=" + type +
                '}';
    }


    public static class Builder {
        private Integer coreCount;
        private Integer socketCount;
        private String boardSerial;
        private String macAddress;
        private String expireDate;
        private Integer type;

        public Builder coreCount(Integer coreCount) {
            this.coreCount = coreCount;
            return this;
        }

        public Builder socketCount(Integer socketCount) {
            this.socketCount = socketCount;
            return this;
        }

        public Builder boardSerial(String boardSerial) {
            this.boardSerial = boardSerial;
            return this;
        }

        public Builder macAddress(String macAddress) {
            this.macAddress = macAddress;
            return this;
        }

        public Builder expireDate(String expireDate) {
            this.expireDate = expireDate;
            return this;
        }

        public Builder type(Integer type) {
            this.type = type;
            return this;
        }

        public LicenseDTO build() {
            return new LicenseDTO(this);
        }
    }
}
