package com.romix.feature.feat521

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat521Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat521UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat521FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat521UserSummary
)

data class Feat521UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat521NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat521Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat521Config = Feat521Config()
) {

    fun loadSnapshot(userId: Long): Feat521NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat521NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat521UserSummary {
        return Feat521UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat521FeedItem> {
        val result = java.util.ArrayList<Feat521FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat521FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat521UiMapper {

    fun mapToUi(model: List<Feat521FeedItem>): Feat521UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat521UiModel(
            header = UiText("Feat521 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat521UiModel =
        Feat521UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat521UiModel =
        Feat521UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat521UiModel =
        Feat521UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat521Service(
    private val repository: Feat521Repository,
    private val uiMapper: Feat521UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat521UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat521UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat521UserItem1(val user: CoreUser, val label: String)
data class Feat521UserItem2(val user: CoreUser, val label: String)
data class Feat521UserItem3(val user: CoreUser, val label: String)
data class Feat521UserItem4(val user: CoreUser, val label: String)
data class Feat521UserItem5(val user: CoreUser, val label: String)
data class Feat521UserItem6(val user: CoreUser, val label: String)
data class Feat521UserItem7(val user: CoreUser, val label: String)
data class Feat521UserItem8(val user: CoreUser, val label: String)
data class Feat521UserItem9(val user: CoreUser, val label: String)
data class Feat521UserItem10(val user: CoreUser, val label: String)

data class Feat521StateBlock1(val state: Feat521UiModel, val checksum: Int)
data class Feat521StateBlock2(val state: Feat521UiModel, val checksum: Int)
data class Feat521StateBlock3(val state: Feat521UiModel, val checksum: Int)
data class Feat521StateBlock4(val state: Feat521UiModel, val checksum: Int)
data class Feat521StateBlock5(val state: Feat521UiModel, val checksum: Int)
data class Feat521StateBlock6(val state: Feat521UiModel, val checksum: Int)
data class Feat521StateBlock7(val state: Feat521UiModel, val checksum: Int)
data class Feat521StateBlock8(val state: Feat521UiModel, val checksum: Int)
data class Feat521StateBlock9(val state: Feat521UiModel, val checksum: Int)
data class Feat521StateBlock10(val state: Feat521UiModel, val checksum: Int)

fun buildFeat521UserItem(user: CoreUser, index: Int): Feat521UserItem1 {
    return Feat521UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat521StateBlock(model: Feat521UiModel): Feat521StateBlock1 {
    return Feat521StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat521UserSummary> {
    val list = java.util.ArrayList<Feat521UserSummary>(users.size)
    for (user in users) {
        list += Feat521UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat521UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat521UiModel {
    val summaries = (0 until count).map {
        Feat521UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat521UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat521UiModel> {
    val models = java.util.ArrayList<Feat521UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat521AnalyticsEvent1(val name: String, val value: String)
data class Feat521AnalyticsEvent2(val name: String, val value: String)
data class Feat521AnalyticsEvent3(val name: String, val value: String)
data class Feat521AnalyticsEvent4(val name: String, val value: String)
data class Feat521AnalyticsEvent5(val name: String, val value: String)
data class Feat521AnalyticsEvent6(val name: String, val value: String)
data class Feat521AnalyticsEvent7(val name: String, val value: String)
data class Feat521AnalyticsEvent8(val name: String, val value: String)
data class Feat521AnalyticsEvent9(val name: String, val value: String)
data class Feat521AnalyticsEvent10(val name: String, val value: String)

fun logFeat521Event1(event: Feat521AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat521Event2(event: Feat521AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat521Event3(event: Feat521AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat521Event4(event: Feat521AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat521Event5(event: Feat521AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat521Event6(event: Feat521AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat521Event7(event: Feat521AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat521Event8(event: Feat521AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat521Event9(event: Feat521AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat521Event10(event: Feat521AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat521Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat521Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat521Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat521Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat521Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat521Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat521Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat521Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat521Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat521Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat521(u: CoreUser): Feat521Projection1 =
    Feat521Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat521Projection1> {
    val list = java.util.ArrayList<Feat521Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat521(u)
    }
    return list
}
