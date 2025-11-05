package com.mateus.encurta_link.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.mateus.encurta_link.documentation.IShortLinkController;
import com.mateus.encurta_link.dto.ShortLink.ShortLinkDtoRequest;
import com.mateus.encurta_link.exceptions.ShortLinkConflictException;
import com.mateus.encurta_link.exceptions.ShortLinkNotFoundException;
import com.mateus.encurta_link.exceptions.UserNotFoundException;
import com.mateus.encurta_link.model.ShortLink;
import com.mateus.encurta_link.service.JwtService;
import com.mateus.encurta_link.service.ShortLinkService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("link")
public class ShortLinkController implements IShortLinkController{
    @Autowired
    private ShortLinkService encurtadorService;

    @Autowired
    private JwtService jwtService;

    @GetMapping("{codigo}")
    public RedirectView redirecionarLink(@PathVariable(name = "codigo") String codigo)
            throws ShortLinkNotFoundException {
        String link = this.encurtadorService.GetLink(codigo);
        return new RedirectView(link);
    }

    @PostMapping("create")
    public ResponseEntity<String> criarShortLink(@Valid @RequestBody  ShortLinkDtoRequest dto,
            @RequestHeader(name = "Authorization") String bearerToken)
            throws ShortLinkConflictException, UserNotFoundException {
        String token = bearerToken.substring("bearer ".length());
        String email = jwtService.extractEmail(token);
        ShortLink shortLink = this.encurtadorService.AddLink(dto, email);

        return ResponseEntity.ok("Short link created with code '" + shortLink.getShortLink() + "'.");
    }

}
