package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class FlagsHollow extends HollowObject {

    public FlagsHollow(FlagsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public boolean _getSearchOnly() {
        return delegate().getSearchOnly(ordinal);
    }

    public Boolean _getSearchOnlyBoxed() {
        return delegate().getSearchOnlyBoxed(ordinal);
    }

    public boolean _getLocalText() {
        return delegate().getLocalText(ordinal);
    }

    public Boolean _getLocalTextBoxed() {
        return delegate().getLocalTextBoxed(ordinal);
    }

    public boolean _getLanguageOverride() {
        return delegate().getLanguageOverride(ordinal);
    }

    public Boolean _getLanguageOverrideBoxed() {
        return delegate().getLanguageOverrideBoxed(ordinal);
    }

    public boolean _getLocalAudio() {
        return delegate().getLocalAudio(ordinal);
    }

    public Boolean _getLocalAudioBoxed() {
        return delegate().getLocalAudioBoxed(ordinal);
    }

    public boolean _getGoLive() {
        return delegate().getGoLive(ordinal);
    }

    public Boolean _getGoLiveBoxed() {
        return delegate().getGoLiveBoxed(ordinal);
    }

    public boolean _getContentApproved() {
        return delegate().getContentApproved(ordinal);
    }

    public Boolean _getContentApprovedBoxed() {
        return delegate().getContentApprovedBoxed(ordinal);
    }

    public boolean _getAutoPlay() {
        return delegate().getAutoPlay(ordinal);
    }

    public Boolean _getAutoPlayBoxed() {
        return delegate().getAutoPlayBoxed(ordinal);
    }

    public DateHollow _getFirstDisplayDate() {
        int refOrdinal = delegate().getFirstDisplayDateOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDateHollow(refOrdinal);
    }

    public MapOfFlagsFirstDisplayDatesHollow _getFirstDisplayDates() {
        int refOrdinal = delegate().getFirstDisplayDatesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getMapOfFlagsFirstDisplayDatesHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public FlagsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected FlagsDelegate delegate() {
        return (FlagsDelegate)delegate;
    }

}