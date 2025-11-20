package com.romix.feature.feat622

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat622Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat622UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat622FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat622UserSummary
)

data class Feat622UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat622NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat622Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat622Config = Feat622Config()
) {

    fun loadSnapshot(userId: Long): Feat622NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat622NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat622UserSummary {
        return Feat622UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat622FeedItem> {
        val result = java.util.ArrayList<Feat622FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat622FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat622UiMapper {

    fun mapToUi(model: List<Feat622FeedItem>): Feat622UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat622UiModel(
            header = UiText("Feat622 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat622UiModel =
        Feat622UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat622UiModel =
        Feat622UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat622UiModel =
        Feat622UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat622Service(
    private val repository: Feat622Repository,
    private val uiMapper: Feat622UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat622UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat622UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat622UserItem1(val user: CoreUser, val label: String)
data class Feat622UserItem2(val user: CoreUser, val label: String)
data class Feat622UserItem3(val user: CoreUser, val label: String)
data class Feat622UserItem4(val user: CoreUser, val label: String)
data class Feat622UserItem5(val user: CoreUser, val label: String)
data class Feat622UserItem6(val user: CoreUser, val label: String)
data class Feat622UserItem7(val user: CoreUser, val label: String)
data class Feat622UserItem8(val user: CoreUser, val label: String)
data class Feat622UserItem9(val user: CoreUser, val label: String)
data class Feat622UserItem10(val user: CoreUser, val label: String)

data class Feat622StateBlock1(val state: Feat622UiModel, val checksum: Int)
data class Feat622StateBlock2(val state: Feat622UiModel, val checksum: Int)
data class Feat622StateBlock3(val state: Feat622UiModel, val checksum: Int)
data class Feat622StateBlock4(val state: Feat622UiModel, val checksum: Int)
data class Feat622StateBlock5(val state: Feat622UiModel, val checksum: Int)
data class Feat622StateBlock6(val state: Feat622UiModel, val checksum: Int)
data class Feat622StateBlock7(val state: Feat622UiModel, val checksum: Int)
data class Feat622StateBlock8(val state: Feat622UiModel, val checksum: Int)
data class Feat622StateBlock9(val state: Feat622UiModel, val checksum: Int)
data class Feat622StateBlock10(val state: Feat622UiModel, val checksum: Int)

fun buildFeat622UserItem(user: CoreUser, index: Int): Feat622UserItem1 {
    return Feat622UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat622StateBlock(model: Feat622UiModel): Feat622StateBlock1 {
    return Feat622StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat622UserSummary> {
    val list = java.util.ArrayList<Feat622UserSummary>(users.size)
    for (user in users) {
        list += Feat622UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat622UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat622UiModel {
    val summaries = (0 until count).map {
        Feat622UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat622UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat622UiModel> {
    val models = java.util.ArrayList<Feat622UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat622AnalyticsEvent1(val name: String, val value: String)
data class Feat622AnalyticsEvent2(val name: String, val value: String)
data class Feat622AnalyticsEvent3(val name: String, val value: String)
data class Feat622AnalyticsEvent4(val name: String, val value: String)
data class Feat622AnalyticsEvent5(val name: String, val value: String)
data class Feat622AnalyticsEvent6(val name: String, val value: String)
data class Feat622AnalyticsEvent7(val name: String, val value: String)
data class Feat622AnalyticsEvent8(val name: String, val value: String)
data class Feat622AnalyticsEvent9(val name: String, val value: String)
data class Feat622AnalyticsEvent10(val name: String, val value: String)

fun logFeat622Event1(event: Feat622AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat622Event2(event: Feat622AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat622Event3(event: Feat622AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat622Event4(event: Feat622AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat622Event5(event: Feat622AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat622Event6(event: Feat622AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat622Event7(event: Feat622AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat622Event8(event: Feat622AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat622Event9(event: Feat622AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat622Event10(event: Feat622AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat622Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat622Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat622Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat622Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat622Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat622Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat622Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat622Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat622Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat622Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat622(u: CoreUser): Feat622Projection1 =
    Feat622Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat622Projection1> {
    val list = java.util.ArrayList<Feat622Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat622(u)
    }
    return list
}
