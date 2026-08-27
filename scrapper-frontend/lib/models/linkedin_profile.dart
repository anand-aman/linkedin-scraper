class HybridLinkedInProfileResponse {
  final String? profileUrl;
  final String? vanityUrl;
  final String? fullName;
  final String? headline;
  final String? description;
  final HybridCurrentPosition? currentPosition;
  final String? location;
  final String? countryCode;
  final int? followerCount;
  final String? connectionCount;
  final String? profilePhotoUrl;
  final String? coverImageUrl;
  final List<String?> languages;
  final List<HybridPost> posts;
  final List<HybridArticle> articles;
  final List<HybridExperience> experiences;
  final List<HybridEducation> education;

  HybridLinkedInProfileResponse({
    this.profileUrl,
    this.vanityUrl,
    this.fullName,
    this.headline,
    this.description,
    this.currentPosition,
    this.location,
    this.countryCode,
    this.followerCount,
    this.connectionCount,
    this.profilePhotoUrl,
    this.coverImageUrl,
    this.languages = const [],
    this.posts = const [],
    this.articles = const [],
    this.experiences = const [],
    this.education = const [],
  });

  factory HybridLinkedInProfileResponse.fromJson(Map<String, dynamic> json) {
    return HybridLinkedInProfileResponse(
      profileUrl: _toNullableString(json['profileUrl']),
      vanityUrl: _toNullableString(json['vanityUrl']),
      fullName: _toNullableString(json['fullName']),
      headline: _toNullableString(json['headline']),
      description: _toNullableString(json['description']),
      location: _toNullableString(json['location']),
      countryCode: _toNullableString(json['countryCode']),
      followerCount: _toInt(json['followerCount']),
      connectionCount: _toNullableString(json['connectionCount']),
      profilePhotoUrl: _toNullableString(json['profilePhotoUrl']),
      coverImageUrl: _toNullableString(json['coverImageUrl']),
      currentPosition: json['currentPosition'] != null
          ? HybridCurrentPosition.fromJson(
              json['currentPosition'] as Map<String, dynamic>,
            )
          : null,
      languages: ((json['languages'] as List?) ?? const [])
          .map((value) => _toNullableString(value))
          .toList(),
      posts: ((json['posts'] as List?) ?? const [])
          .map((e) => HybridPost.fromJson(e as Map<String, dynamic>))
          .toList(),
      articles: ((json['articles'] as List?) ?? const [])
          .map((e) => HybridArticle.fromJson(e as Map<String, dynamic>))
          .toList(),
      experiences: ((json['experiences'] as List?) ?? const [])
          .map((e) => HybridExperience.fromJson(e as Map<String, dynamic>))
          .toList(),
      education: ((json['education'] as List?) ?? const [])
          .map((e) => HybridEducation.fromJson(e as Map<String, dynamic>))
          .toList(),
    );
  }

  static String? _toNullableString(dynamic value) {
    if (value == null) {
      return null;
    }

    final text = value.toString().trim();
    return text.isEmpty ? null : text;
  }

  static int? _toInt(dynamic value) {
    final normalized = _toNullableString(value);
    if (normalized == null) {
      return null;
    }
    if (value is int) {
      return value;
    }

    return int.tryParse(normalized.replaceAll(',', ''));
  }
}

class HybridCurrentPosition {
  final String? companyName;
  final String? companyLinkedinUrl;
  final String? companyLogoUrl;
  final String? jobTitle;

  HybridCurrentPosition({
    this.companyName,
    this.companyLinkedinUrl,
    this.companyLogoUrl,
    this.jobTitle,
  });

  factory HybridCurrentPosition.fromJson(Map<String, dynamic> json) {
    return HybridCurrentPosition(
      companyName:
          HybridLinkedInProfileResponse._toNullableString(json['companyName']),
      companyLinkedinUrl: HybridLinkedInProfileResponse._toNullableString(
        json['companyLinkedinUrl'],
      ),
      companyLogoUrl: HybridLinkedInProfileResponse._toNullableString(
        json['companyLogoUrl'],
      ),
      jobTitle: HybridLinkedInProfileResponse._toNullableString(json['jobTitle']),
    );
  }
}

class HybridExperience {
  final String? companyName;
  final String? companyUrl;
  final String? jobTitle;
  final String? description;
  final String? location;

  HybridExperience({
    this.companyName,
    this.companyUrl,
    this.jobTitle,
    this.description,
    this.location,
  });

  factory HybridExperience.fromJson(Map<String, dynamic> json) {
    return HybridExperience(
      companyName:
          HybridLinkedInProfileResponse._toNullableString(json['companyName']),
      companyUrl:
          HybridLinkedInProfileResponse._toNullableString(json['companyUrl']),
      jobTitle:
          HybridLinkedInProfileResponse._toNullableString(json['jobTitle']),
      description:
          HybridLinkedInProfileResponse._toNullableString(json['description']),
      location:
          HybridLinkedInProfileResponse._toNullableString(json['location']),
    );
  }
}

class HybridPost {
  final String? datePublished;
  final int? likeCount;
  final String? postUrl;
  final String? text;

  HybridPost({
    this.datePublished,
    this.likeCount,
    this.postUrl,
    this.text,
  });

  factory HybridPost.fromJson(Map<String, dynamic> json) {
    return HybridPost(
      datePublished:
          HybridLinkedInProfileResponse._toNullableString(json['datePublished']),
      likeCount: HybridLinkedInProfileResponse._toInt(json['likeCount']),
      postUrl: HybridLinkedInProfileResponse._toNullableString(json['postUrl']),
      text: HybridLinkedInProfileResponse._toNullableString(json['text']),
    );
  }
}

class HybridArticle {
  final String? title;
  final String? url;
  final String? publishedDate;
  final String? image;
  final int? likes;

  HybridArticle({
    this.title,
    this.url,
    this.publishedDate,
    this.image,
    this.likes,
  });

  factory HybridArticle.fromJson(Map<String, dynamic> json) {
    return HybridArticle(
      title: HybridLinkedInProfileResponse._toNullableString(json['title']),
      url: HybridLinkedInProfileResponse._toNullableString(json['url']),
      publishedDate:
          HybridLinkedInProfileResponse._toNullableString(json['publishedDate']),
      image: HybridLinkedInProfileResponse._toNullableString(json['image']),
      likes: HybridLinkedInProfileResponse._toInt(json['likes']),
    );
  }
}

class HybridEducation {
  final String? institution;
  final String? institutionUrl;
  final String? startDate;
  final String? endDate;

  HybridEducation({
    this.institution,
    this.institutionUrl,
    this.startDate,
    this.endDate,
  });

  factory HybridEducation.fromJson(Map<String, dynamic> json) {
    return HybridEducation(
      institution:
          HybridLinkedInProfileResponse._toNullableString(json['institution']),
      institutionUrl:
          HybridLinkedInProfileResponse._toNullableString(json['institutionUrl']),
      startDate:
          HybridLinkedInProfileResponse._toNullableString(json['startDate']),
      endDate: HybridLinkedInProfileResponse._toNullableString(json['endDate']),
    );
  }
}
