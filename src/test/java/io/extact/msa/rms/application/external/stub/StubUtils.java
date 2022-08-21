package io.extact.msa.rms.application.external.stub;

import javax.enterprise.inject.spi.CDI;

public class StubUtils {
    public static void refreshData() {
        var itemStubInstance = CDI.current().select(RentalItemApiRemoteStub.class);
        if (itemStubInstance.isResolvable()) {
            itemStubInstance.get().init();
        }
        var reservationStubInstance = CDI.current().select(ReservationApiRemoteStub.class);
        if (reservationStubInstance.isResolvable()) {
            reservationStubInstance.get().init();
        }
        var userStubInstance = CDI.current().select(UserAccountApiRemoteStub.class);
        if (userStubInstance.isResolvable()) {
            userStubInstance.get().init();
        }
    }
}
