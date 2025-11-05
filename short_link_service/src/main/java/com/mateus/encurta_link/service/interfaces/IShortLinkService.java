package com.mateus.encurta_link.service.interfaces;

import com.mateus.encurta_link.dto.ShortLink.ShortLinkDtoRequest;
import com.mateus.encurta_link.exceptions.ShortLinkConflictException;
import com.mateus.encurta_link.exceptions.ShortLinkNotFoundException;
import com.mateus.encurta_link.exceptions.UserNotFoundException;
import com.mateus.encurta_link.model.ShortLink;

public interface IShortLinkService {

    public String GetLink(String code) throws ShortLinkNotFoundException;

    public ShortLink AddLink(ShortLinkDtoRequest dto, String email)
            throws ShortLinkConflictException, UserNotFoundException;

    public void removeExpiredLinks();
}
