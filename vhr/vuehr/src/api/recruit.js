import {getRequest, postRequest, putRequest, deleteRequest} from '@/utils/api';

// 简历管理API
export const getResumeList = (params) => {
    return getRequest('/recruit/resume/', params);
};

export const addResume = (data) => {
    return postRequest('/recruit/resume/', data);
};

export const updateResume = (data) => {
    return putRequest('/recruit/resume/', data);
};

export const deleteResume = (id) => {
    return deleteRequest('/recruit/resume/' + id);
};

export const getResumeById = (id) => {
    return getRequest('/recruit/resume/' + id);
};

export const updateResumeStatus = (id, status) => {
    return putRequest('/recruit/resume/status/' + id + '?status=' + status);
};

export const getResumeByStatus = (status) => {
    return getRequest('/recruit/resume/status/' + status);
};

export const exportResume = () => {
    return getRequest('/recruit/resume/export');
};

export const importResume = (data) => {
    return postRequest('/recruit/resume/import', data);
};

// 面试管理API
export const getInterviewList = (params) => {
    return getRequest('/recruit/interview/', params);
};

export const addInterview = (data) => {
    return postRequest('/recruit/interview/', data);
};

export const batchAddInterview = (data) => {
    return postRequest('/recruit/interview/batch', data);
};

export const updateInterview = (data) => {
    return putRequest('/recruit/interview/', data);
};

export const deleteInterview = (id) => {
    return deleteRequest('/recruit/interview/' + id);
};

export const getInterviewById = (id) => {
    return getRequest('/recruit/interview/' + id);
};

export const getInterviewByResumeId = (resumeId) => {
    return getRequest('/recruit/interview/resume/' + resumeId);
};

export const updateInterviewStatus = (id, status) => {
    return putRequest('/recruit/interview/status/' + id + '?status=' + status);
};

export const updateInterviewEvaluation = (id, data) => {
    return putRequest('/recruit/interview/evaluation/' + id, data);
};

export const getInterviewStatistics = (startDate, endDate) => {
    return getRequest('/recruit/interview/statistics?startDate=' + startDate + '&endDate=' + endDate);
};

// Offer管理API
export const getOfferList = (params) => {
    return getRequest('/recruit/offer/', params);
};

export const addOffer = (data) => {
    return postRequest('/recruit/offer/', data);
};

export const updateOffer = (data) => {
    return putRequest('/recruit/offer/', data);
};

export const deleteOffer = (id) => {
    return deleteRequest('/recruit/offer/' + id);
};

export const getOfferById = (id) => {
    return getRequest('/recruit/offer/' + id);
};

export const getOfferByResumeId = (resumeId) => {
    return getRequest('/recruit/offer/resume/' + resumeId);
};

export const sendOffer = (id) => {
    return putRequest('/recruit/offer/send/' + id);
};

export const updateOfferStatus = (id, status, rejectReason) => {
    let url = '/recruit/offer/status/' + id + '?status=' + status;
    if (rejectReason) {
        url += '&rejectReason=' + encodeURIComponent(rejectReason);
    }
    return putRequest(url);
};

export const updateOfferViewStatus = (id) => {
    return putRequest('/recruit/offer/view/' + id);
};

export const getExpiringOffers = (date) => {
    return getRequest('/recruit/offer/expiring?date=' + date);
};

export const getUpcomingEntryOffers = (startDate, endDate) => {
    return getRequest('/recruit/offer/upcoming?startDate=' + startDate + '&endDate=' + endDate);
};
