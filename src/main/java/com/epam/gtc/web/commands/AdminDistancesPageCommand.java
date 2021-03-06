package com.epam.gtc.web.commands;

import com.epam.gtc.Path;
import com.epam.gtc.exceptions.AppException;
import com.epam.gtc.exceptions.CommandException;
import com.epam.gtc.exceptions.ServiceException;
import com.epam.gtc.services.CityService;
import com.epam.gtc.services.DistanceService;
import com.epam.gtc.services.domains.DistanceDomain;
import com.epam.gtc.utils.Method;
import com.epam.gtc.web.models.CityModel;
import com.epam.gtc.web.models.DistanceModel;
import com.epam.gtc.web.models.builders.CityModelBuilder;
import com.epam.gtc.web.models.builders.DistanceModelBuilder;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Admin distance page command.
 *
 * @author Fazliddin Makhsudov
 */
public class AdminDistancesPageCommand implements Command {

    private static final long serialVersionUID = -3071536593627692473L;

    private static final Logger LOG = Logger.getLogger(AdminDistancesPageCommand.class);
    private final DistanceService distanceService;
    private final CityService cityService;

    public AdminDistancesPageCommand(DistanceService distanceService, CityService cityService) {
        this.distanceService = distanceService;
        this.cityService = cityService;
    }

    @Override
    public final String execute(final HttpServletRequest request, final HttpServletResponse response)
            throws AppException {
        LOG.debug("AdminDistancesPageCommand starts");
        String forward = handleRequest(request);
        supplyRequestWithCities(request);
        LOG.debug("AdminDistancesPageCommand finished");
        return forward;
    }

    private String handleRequest(final HttpServletRequest request) throws AppException {
        int distancesNumber = distanceService.countAllDistances();
        LOG.trace("Number of distances : " + distancesNumber);
        Optional<String> optionalPage = Optional.ofNullable(request.getParameter("page"));
        LOG.trace("optional page : " + optionalPage);
        Optional<String> optionalItemsPerPage = Optional.ofNullable(request.getParameter("itemsPerPage"));
        LOG.trace("optional items per page : " + optionalItemsPerPage);

        int page = optionalPage.isPresent() && Validator.isValidNumber(optionalPage.get())
                ? optionalPage.map(Integer::parseInt).orElse(1) : 1;

        int itemsPerPage = optionalItemsPerPage.isPresent() && Validator.isValidNumber(optionalItemsPerPage.get()) ?
                optionalItemsPerPage.map(Integer::parseInt).orElse(5) : 5;

        String forward = Path.PAGE_ADMIN_DISTANCES;
        if (Method.isPost(request)) {
            forward = doPost(request, distanceService, page, itemsPerPage);
        }
        List<DistanceModel> distanceModels = new DistanceModelBuilder().create(distanceService.findAll(page, itemsPerPage));
        LOG.trace("Distances : " + distanceModels);
        request.setAttribute("page", page);
        request.setAttribute("itemsPerPage", itemsPerPage);
        request.setAttribute("currentPage", page);
        request.setAttribute("distancesNumber", distancesNumber);
        request.setAttribute("adminDistances", distanceModels);
        return forward;
    }

    private String doPost(HttpServletRequest request, DistanceService distanceService, int page, int itemsPerPage) throws ServiceException {
        String forward = String.format("%s&page=%s&itemsPerPage=%s", Path.COMMAND_ADMIN_DISTANCES_PAGE,
                page, itemsPerPage);
        StringBuilder errorDistances = new StringBuilder();
        boolean errorFlag = false;

        LOG.trace("Method is Post");
        request.getSession().removeAttribute("errorDistance");

        String action = request.getParameter(FormRequestParametersNames.ACTION);
        LOG.trace("Action --> " + action);
        if (!Validator.isValidString(action)) {
            errorFlag = true;
            errorDistances.append("Invalid action").append("<br/>");
        }

        boolean isRemoveMethod = action.equalsIgnoreCase("remove");

        String fromCityIdString = request.getParameter(FormRequestParametersNames.DISTANCE_FROM_CITY_ID);
        LOG.trace("Distance from city id --> " + fromCityIdString);
        if (!isRemoveMethod && !Validator.isValidNumber(fromCityIdString)) {
            errorFlag = true;
            errorDistances.append("Invalid city from id").append("<br/>");
        }

        String toCityIdString = request.getParameter(FormRequestParametersNames.DISTANCE_TO_CITY_ID);
        LOG.trace("Distance to city id --> " + toCityIdString);
        if (!isRemoveMethod && !Validator.isValidNumber(toCityIdString)) {
            errorFlag = true;
            errorDistances.append("Invalid city to id").append("<br/>");
        }

        String distanceIdString = request.getParameter(FormRequestParametersNames.DISTANCE_ID);
        LOG.trace("Distance id --> " + distanceIdString);
        if (!action.equalsIgnoreCase("add") && !Validator.isValidNumber(distanceIdString)) {
            errorFlag = true;
            errorDistances.append("Invalid distance id").append("<br/>");
        }

        String distanceString = request.getParameter(FormRequestParametersNames.DISTANCE_DISTANCE);
        LOG.trace("Distance between cities --> " + distanceString);
        if (!isRemoveMethod && !Validator.isValidNumber(distanceString)) {
            errorFlag = true;
            errorDistances.append("Invalid distance").append("<br/>");
        }

        if (errorFlag) {
            request.getSession().setAttribute("errorDistance", errorDistances.toString());
            return forward;
        }

        doPostIfAllRight(request, distanceService, action, isRemoveMethod, fromCityIdString,
                toCityIdString, distanceIdString, distanceString);
        return forward;
    }

    private void doPostIfAllRight(HttpServletRequest request, DistanceService distanceService, String action, boolean isRemoveMethod, String fromCityIdString, String toCityIdString, String distanceIdString, String distanceString) throws ServiceException {
        int fromCityId = isRemoveMethod ? -1 : Integer.parseInt(fromCityIdString);
        int toCityId = isRemoveMethod ? -1 : Integer.parseInt(toCityIdString);
        double distance = isRemoveMethod ? -1d : Double.parseDouble(distanceString);
        int distanceId = action.equalsIgnoreCase("add") ? -1 : Integer.parseInt(distanceIdString);
        switch (action) {
            case "add":
                if (Objects.isNull(distanceService.find(fromCityId, toCityId))) {
                    DistanceDomain newDistanceDomain = new DistanceDomain();
                    newDistanceDomain.setFromCityId(fromCityId);
                    newDistanceDomain.setToCityId(toCityId);
                    newDistanceDomain.setDistance(distance);
                    int newId = distanceService.add(newDistanceDomain);
                    LOG.trace("Added status(new id) --> " + newId);
                } else {
                    request.getSession().setAttribute("errorDistance", "Distance between these cities exists already.");
                }
                break;
            case "save":
                DistanceDomain distanceDomain = distanceService.find(distanceId);
                distanceDomain.setFromCityId(fromCityId);
                distanceDomain.setToCityId(toCityId);
                distanceDomain.setDistance(distance);
                boolean savedFlag = distanceService.save(distanceDomain);
                LOG.trace("Saved status --> " + savedFlag);
                break;
            case "remove":
                boolean removedFlag = distanceService.remove(distanceId);
                LOG.trace("Removed status --> " + removedFlag);
                break;
        }
    }

    private void supplyRequestWithCities(HttpServletRequest request) {
        try {
            List<CityModel> cityModels = new CityModelBuilder().create(cityService.findAll());
            List<String> cityNames = cityModels.stream()
                    .map(CityModel::getName)
                    .collect(Collectors.toList());
            Map<Integer, String> citiesMap = cityModels.stream()
                    .collect(Collectors.toMap(CityModel::getId,
                            CityModel::getName));
            request.setAttribute("citiesNames", cityNames);
            request.setAttribute("citiesMap", citiesMap);

            LOG.trace(String.format("Cities names --> %s", cityNames));
            LOG.trace(String.format("Cities map --> %s", citiesMap));
            LOG.trace("all cities has been downloaded to context");
        } catch (AppException e) {
            LOG.trace("city downloading is failed", e);
            throw new CommandException(e.getMessage(), e);
        }
    }
}