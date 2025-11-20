package com.romix.feature.feat624

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat624Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat624UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat624FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat624UserSummary
)

data class Feat624UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat624NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat624Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat624Config = Feat624Config()
) {

    fun loadSnapshot(userId: Long): Feat624NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat624NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat624UserSummary {
        return Feat624UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat624FeedItem> {
        val result = java.util.ArrayList<Feat624FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat624FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat624UiMapper {

    fun mapToUi(model: List<Feat624FeedItem>): Feat624UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat624UiModel(
            header = UiText("Feat624 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat624UiModel =
        Feat624UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat624UiModel =
        Feat624UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat624UiModel =
        Feat624UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat624Service(
    private val repository: Feat624Repository,
    private val uiMapper: Feat624UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat624UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat624UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat624UserItem1(val user: CoreUser, val label: String)
data class Feat624UserItem2(val user: CoreUser, val label: String)
data class Feat624UserItem3(val user: CoreUser, val label: String)
data class Feat624UserItem4(val user: CoreUser, val label: String)
data class Feat624UserItem5(val user: CoreUser, val label: String)
data class Feat624UserItem6(val user: CoreUser, val label: String)
data class Feat624UserItem7(val user: CoreUser, val label: String)
data class Feat624UserItem8(val user: CoreUser, val label: String)
data class Feat624UserItem9(val user: CoreUser, val label: String)
data class Feat624UserItem10(val user: CoreUser, val label: String)

data class Feat624StateBlock1(val state: Feat624UiModel, val checksum: Int)
data class Feat624StateBlock2(val state: Feat624UiModel, val checksum: Int)
data class Feat624StateBlock3(val state: Feat624UiModel, val checksum: Int)
data class Feat624StateBlock4(val state: Feat624UiModel, val checksum: Int)
data class Feat624StateBlock5(val state: Feat624UiModel, val checksum: Int)
data class Feat624StateBlock6(val state: Feat624UiModel, val checksum: Int)
data class Feat624StateBlock7(val state: Feat624UiModel, val checksum: Int)
data class Feat624StateBlock8(val state: Feat624UiModel, val checksum: Int)
data class Feat624StateBlock9(val state: Feat624UiModel, val checksum: Int)
data class Feat624StateBlock10(val state: Feat624UiModel, val checksum: Int)

fun buildFeat624UserItem(user: CoreUser, index: Int): Feat624UserItem1 {
    return Feat624UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat624StateBlock(model: Feat624UiModel): Feat624StateBlock1 {
    return Feat624StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat624UserSummary> {
    val list = java.util.ArrayList<Feat624UserSummary>(users.size)
    for (user in users) {
        list += Feat624UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat624UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat624UiModel {
    val summaries = (0 until count).map {
        Feat624UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat624UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat624UiModel> {
    val models = java.util.ArrayList<Feat624UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat624AnalyticsEvent1(val name: String, val value: String)
data class Feat624AnalyticsEvent2(val name: String, val value: String)
data class Feat624AnalyticsEvent3(val name: String, val value: String)
data class Feat624AnalyticsEvent4(val name: String, val value: String)
data class Feat624AnalyticsEvent5(val name: String, val value: String)
data class Feat624AnalyticsEvent6(val name: String, val value: String)
data class Feat624AnalyticsEvent7(val name: String, val value: String)
data class Feat624AnalyticsEvent8(val name: String, val value: String)
data class Feat624AnalyticsEvent9(val name: String, val value: String)
data class Feat624AnalyticsEvent10(val name: String, val value: String)

fun logFeat624Event1(event: Feat624AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat624Event2(event: Feat624AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat624Event3(event: Feat624AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat624Event4(event: Feat624AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat624Event5(event: Feat624AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat624Event6(event: Feat624AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat624Event7(event: Feat624AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat624Event8(event: Feat624AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat624Event9(event: Feat624AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat624Event10(event: Feat624AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat624Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat624Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat624Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat624Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat624Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat624Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat624Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat624Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat624Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat624Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat624(u: CoreUser): Feat624Projection1 =
    Feat624Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat624Projection1> {
    val list = java.util.ArrayList<Feat624Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat624(u)
    }
    return list
}
