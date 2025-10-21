package fr.utbm.ciad.wprest.views;

import fr.utbm.ciad.labmanager.Constants;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BackendVersionService {

    /**
     * Gets the version of the backend API {@link Constants#MANAGER_MAJOR_VERSION}.
     *
     * @return - the version of the backend
     */
    @Operation(summary = "Gets the version of the backend API.", description = "Use this in the frontend to know the version of the backend API.", tags = {"Utils"})
    @GetMapping("/version")
    public String getVersion() {
        return Constants.MANAGER_MAJOR_VERSION;
    }
}
