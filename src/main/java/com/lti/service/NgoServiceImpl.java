package com.lti.service;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lti.dao.NgoDao;
import com.lti.dto.NgoDocRegister;
import com.lti.dto.NgoLogin;
import com.lti.dto.NgoStatusDTO;
import com.lti.dto.UserProfileDto;
import com.lti.entity.Accomodation;
import com.lti.entity.Course;
import com.lti.entity.Ngo;
import com.lti.entity.NgoDocuments;

@Service
public class NgoServiceImpl implements NgoService {

	@Autowired
	NgoDao dao;

	@Override
	public Ngo register(Ngo ngo) {
		System.out.println("Shreyanshu " + dao);
		return dao.register(ngo);
	}

	@Override
	public Ngo login(NgoLogin ngoLogin) {
		return dao.login(ngoLogin);
	}

	public boolean verifyNgo(int ngoId) {
		return dao.verifyNgo(ngoId);
	}

	public NgoStatusDTO getNgoStatus(int ngoId) {
		Ngo ngo = dao.getNgoById(ngoId);
		List<Course> courses = ngo.getCourse();
		NgoStatusDTO ngoStatus = new NgoStatusDTO();
		ngoStatus.courses = courses.size();
		for (Course course : courses) {
			ngoStatus.learners += course.getEnrollments().size();
			if (course.getStartDate().isAfter(LocalDate.now()) && (ngoStatus.nextCourseStarts == null
					|| course.getStartDate().isBefore(ngoStatus.nextCourseStarts))) {
				ngoStatus.nextCourseStarts = course.getStartDate();
			}
			if (course.getStartDate().isBefore(LocalDate.now().minusMonths(course.getDurationMonth()))) {
				ngoStatus.courseFinished++;
			}
		}

		List<Accomodation> accomodations = ngo.getAccomodation();
		ngoStatus.accomodations = accomodations.size();

		for (Accomodation accomodation : accomodations) {
			ngoStatus.residents += accomodation.getAccomodation().size();
			if (accomodation.isDayCareCentre()) {
				ngoStatus.dayCareCenters++;
			}
		}
		return ngoStatus;
	}

	public NgoDocuments postNgoDocument(NgoDocRegister docRegister) {
		// TODO Auto-generated method stub
		NgoDocuments ngoDocuments = new NgoDocuments();
		ngoDocuments.setCertificateLink(docRegister.getCertificateLink());
		ngoDocuments.setCertificateNo(docRegister.getCertificateNo());
		ngoDocuments.setNgo(dao.getNgoById(docRegister.getNgoId()));
		NgoDocuments savedNgoDoc = dao.registerDoc(ngoDocuments);
		savedNgoDoc.getNgo().setAccomodation(null);
		return savedNgoDoc;
	}

	public NgoDocuments getNgoDocument(int ngoId) {
		dao.getNgoById(ngoId);
		return dao.getNgoById(ngoId).getNgoDoc();
	}


}
