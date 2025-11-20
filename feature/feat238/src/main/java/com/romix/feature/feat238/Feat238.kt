package com.romix.feature.feat238

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat238Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat238UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat238FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat238UserSummary
)

data class Feat238UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat238NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat238Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat238Config = Feat238Config()
) {

    fun loadSnapshot(userId: Long): Feat238NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat238NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat238UserSummary {
        return Feat238UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat238FeedItem> {
        val result = java.util.ArrayList<Feat238FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat238FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat238UiMapper {

    fun mapToUi(model: List<Feat238FeedItem>): Feat238UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat238UiModel(
            header = UiText("Feat238 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat238UiModel =
        Feat238UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat238UiModel =
        Feat238UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat238UiModel =
        Feat238UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat238Service(
    private val repository: Feat238Repository,
    private val uiMapper: Feat238UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat238UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat238UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat238UserItem1(val user: CoreUser, val label: String)
data class Feat238UserItem2(val user: CoreUser, val label: String)
data class Feat238UserItem3(val user: CoreUser, val label: String)
data class Feat238UserItem4(val user: CoreUser, val label: String)
data class Feat238UserItem5(val user: CoreUser, val label: String)
data class Feat238UserItem6(val user: CoreUser, val label: String)
data class Feat238UserItem7(val user: CoreUser, val label: String)
data class Feat238UserItem8(val user: CoreUser, val label: String)
data class Feat238UserItem9(val user: CoreUser, val label: String)
data class Feat238UserItem10(val user: CoreUser, val label: String)

data class Feat238StateBlock1(val state: Feat238UiModel, val checksum: Int)
data class Feat238StateBlock2(val state: Feat238UiModel, val checksum: Int)
data class Feat238StateBlock3(val state: Feat238UiModel, val checksum: Int)
data class Feat238StateBlock4(val state: Feat238UiModel, val checksum: Int)
data class Feat238StateBlock5(val state: Feat238UiModel, val checksum: Int)
data class Feat238StateBlock6(val state: Feat238UiModel, val checksum: Int)
data class Feat238StateBlock7(val state: Feat238UiModel, val checksum: Int)
data class Feat238StateBlock8(val state: Feat238UiModel, val checksum: Int)
data class Feat238StateBlock9(val state: Feat238UiModel, val checksum: Int)
data class Feat238StateBlock10(val state: Feat238UiModel, val checksum: Int)

fun buildFeat238UserItem(user: CoreUser, index: Int): Feat238UserItem1 {
    return Feat238UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat238StateBlock(model: Feat238UiModel): Feat238StateBlock1 {
    return Feat238StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat238UserSummary> {
    val list = java.util.ArrayList<Feat238UserSummary>(users.size)
    for (user in users) {
        list += Feat238UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat238UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat238UiModel {
    val summaries = (0 until count).map {
        Feat238UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat238UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat238UiModel> {
    val models = java.util.ArrayList<Feat238UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat238AnalyticsEvent1(val name: String, val value: String)
data class Feat238AnalyticsEvent2(val name: String, val value: String)
data class Feat238AnalyticsEvent3(val name: String, val value: String)
data class Feat238AnalyticsEvent4(val name: String, val value: String)
data class Feat238AnalyticsEvent5(val name: String, val value: String)
data class Feat238AnalyticsEvent6(val name: String, val value: String)
data class Feat238AnalyticsEvent7(val name: String, val value: String)
data class Feat238AnalyticsEvent8(val name: String, val value: String)
data class Feat238AnalyticsEvent9(val name: String, val value: String)
data class Feat238AnalyticsEvent10(val name: String, val value: String)

fun logFeat238Event1(event: Feat238AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat238Event2(event: Feat238AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat238Event3(event: Feat238AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat238Event4(event: Feat238AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat238Event5(event: Feat238AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat238Event6(event: Feat238AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat238Event7(event: Feat238AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat238Event8(event: Feat238AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat238Event9(event: Feat238AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat238Event10(event: Feat238AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat238Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat238Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat238Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat238Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat238Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat238Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat238Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat238Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat238Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat238Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat238(u: CoreUser): Feat238Projection1 =
    Feat238Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat238Projection1> {
    val list = java.util.ArrayList<Feat238Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat238(u)
    }
    return list
}
