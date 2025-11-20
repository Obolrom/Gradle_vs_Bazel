package com.romix.feature.feat551

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat551Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat551UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat551FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat551UserSummary
)

data class Feat551UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat551NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat551Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat551Config = Feat551Config()
) {

    fun loadSnapshot(userId: Long): Feat551NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat551NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat551UserSummary {
        return Feat551UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat551FeedItem> {
        val result = java.util.ArrayList<Feat551FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat551FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat551UiMapper {

    fun mapToUi(model: List<Feat551FeedItem>): Feat551UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat551UiModel(
            header = UiText("Feat551 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat551UiModel =
        Feat551UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat551UiModel =
        Feat551UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat551UiModel =
        Feat551UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat551Service(
    private val repository: Feat551Repository,
    private val uiMapper: Feat551UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat551UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat551UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat551UserItem1(val user: CoreUser, val label: String)
data class Feat551UserItem2(val user: CoreUser, val label: String)
data class Feat551UserItem3(val user: CoreUser, val label: String)
data class Feat551UserItem4(val user: CoreUser, val label: String)
data class Feat551UserItem5(val user: CoreUser, val label: String)
data class Feat551UserItem6(val user: CoreUser, val label: String)
data class Feat551UserItem7(val user: CoreUser, val label: String)
data class Feat551UserItem8(val user: CoreUser, val label: String)
data class Feat551UserItem9(val user: CoreUser, val label: String)
data class Feat551UserItem10(val user: CoreUser, val label: String)

data class Feat551StateBlock1(val state: Feat551UiModel, val checksum: Int)
data class Feat551StateBlock2(val state: Feat551UiModel, val checksum: Int)
data class Feat551StateBlock3(val state: Feat551UiModel, val checksum: Int)
data class Feat551StateBlock4(val state: Feat551UiModel, val checksum: Int)
data class Feat551StateBlock5(val state: Feat551UiModel, val checksum: Int)
data class Feat551StateBlock6(val state: Feat551UiModel, val checksum: Int)
data class Feat551StateBlock7(val state: Feat551UiModel, val checksum: Int)
data class Feat551StateBlock8(val state: Feat551UiModel, val checksum: Int)
data class Feat551StateBlock9(val state: Feat551UiModel, val checksum: Int)
data class Feat551StateBlock10(val state: Feat551UiModel, val checksum: Int)

fun buildFeat551UserItem(user: CoreUser, index: Int): Feat551UserItem1 {
    return Feat551UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat551StateBlock(model: Feat551UiModel): Feat551StateBlock1 {
    return Feat551StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat551UserSummary> {
    val list = java.util.ArrayList<Feat551UserSummary>(users.size)
    for (user in users) {
        list += Feat551UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat551UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat551UiModel {
    val summaries = (0 until count).map {
        Feat551UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat551UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat551UiModel> {
    val models = java.util.ArrayList<Feat551UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat551AnalyticsEvent1(val name: String, val value: String)
data class Feat551AnalyticsEvent2(val name: String, val value: String)
data class Feat551AnalyticsEvent3(val name: String, val value: String)
data class Feat551AnalyticsEvent4(val name: String, val value: String)
data class Feat551AnalyticsEvent5(val name: String, val value: String)
data class Feat551AnalyticsEvent6(val name: String, val value: String)
data class Feat551AnalyticsEvent7(val name: String, val value: String)
data class Feat551AnalyticsEvent8(val name: String, val value: String)
data class Feat551AnalyticsEvent9(val name: String, val value: String)
data class Feat551AnalyticsEvent10(val name: String, val value: String)

fun logFeat551Event1(event: Feat551AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat551Event2(event: Feat551AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat551Event3(event: Feat551AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat551Event4(event: Feat551AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat551Event5(event: Feat551AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat551Event6(event: Feat551AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat551Event7(event: Feat551AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat551Event8(event: Feat551AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat551Event9(event: Feat551AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat551Event10(event: Feat551AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat551Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat551Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat551Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat551Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat551Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat551Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat551Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat551Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat551Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat551Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat551(u: CoreUser): Feat551Projection1 =
    Feat551Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat551Projection1> {
    val list = java.util.ArrayList<Feat551Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat551(u)
    }
    return list
}
